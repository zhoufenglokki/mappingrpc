package github.mappingrpc.core.event;

import github.mappingrpc.core.constant.ClientDaemonThreadEventType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimerAndEventDaemonThread extends Thread {
	static Logger log = LoggerFactory.getLogger(TimerAndEventDaemonThread.class);

	private volatile boolean toStop = false;
	private BlockingQueue<ClientDaemonThreadEvent> blockingQueue;
	private List<Runnable> timerJobList = new CopyOnWriteArrayList<Runnable>();
	private Map<Byte, Runnable> eventHandlerMap = new ConcurrentHashMap<Byte, Runnable>();

	public TimerAndEventDaemonThread(BlockingQueue<ClientDaemonThreadEvent> blockingQueue) {
		super("MappingPackageClient-Daemon");
		setDaemon(true);
		this.blockingQueue = blockingQueue;
	}

	public void close() {
		toStop = true;
		blockingQueue.offer(new ClientDaemonThreadEvent(ClientDaemonThreadEventType.closeDaemonThread));
	}

	@Override
	public void run() {
		while (true) {
			long startTime = System.currentTimeMillis();
			for (Runnable job : timerJobList) {
				try {
					job.run();
				} catch (Throwable ex) {
					log.error(ex.getMessage(), ex);
					continue;
				}
			}
			long elapsedTime = System.currentTimeMillis() - startTime;
			if (elapsedTime > 500) {
				elapsedTime = 500;
			}

			try {
				ClientDaemonThreadEvent event = blockingQueue.poll(501 - elapsedTime, TimeUnit.MILLISECONDS);

				if (isInterrupted()) {
					return;
				}
				if (toStop) {
					return;
				}
				if(event != null){
					Runnable job = eventHandlerMap.get(event.getEventType());
					job.run();
				}
			} catch (InterruptedException ex) {
				break;
			} catch (Throwable ex) {
				log.error(ex.getMessage(), ex);
				continue;
			}
		}
	}

	public TimerAndEventDaemonThread addTimerJob(Runnable timerJob) {
		this.timerJobList.add(timerJob);
		return this;
	}

	public TimerAndEventDaemonThread clearTimerJobList() {
		timerJobList.clear();
		return this;
	}

	public TimerAndEventDaemonThread addEventHanler(byte eventType, Runnable eventHanler) {
		eventHandlerMap.put(eventType, eventHanler);
		return this;
	}

}
