package github.mappingrpc.core.io.custompackage;

import github.mappingrpc.api.constant.Feature1;
import github.mappingrpc.api.exception.TimeoutException;
import github.mappingrpc.core.constant.BossThreadEventType;
import github.mappingrpc.core.event.BossThreadEvent;
import github.mappingrpc.core.io.commonhandler.DisconnectDetectHandler;
import github.mappingrpc.core.io.wamp.domain.command.CallCommand;
import github.mappingrpc.core.metadata.MetaHolder;
import github.mappingrpc.core.metadata.ServerMeta;
import github.mappingrpc.core.rpc.CallResultFuture;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MappingPackageClient implements Closeable {
	static Logger log = LoggerFactory.getLogger(MappingPackageClient.class);

	private MetaHolder metaHolder;
	private List<ServerMeta> serverList = new ArrayList<>();
	private byte reconnectCountWhenSendRpc = 3;
	private boolean needKeepConnection = true;

	volatile Channel channel = null;
	AtomicBoolean isReconnecting = new AtomicBoolean(false);

	HeartbeatThread heartbeatRunner = null;
	volatile boolean heartbeatIsStarted = false;
	AtomicBoolean heartbeatIsStarting = new AtomicBoolean(false);

	// FIXME not share queue between connection
	BlockingQueue<BossThreadEvent> bossThreadEventQueue = new LinkedBlockingQueue<BossThreadEvent>(100);

	public MappingPackageClient(MetaHolder metaHolder, Map<String, String> serverMap) {
		this.metaHolder = metaHolder;

		for (Map.Entry<String, String> serverPair : serverMap.entrySet()) {
			ServerMeta serverMeta = new ServerMeta(serverPair.getKey(), Short.parseShort(serverPair.getValue()));
			serverList.add(serverMeta);

			if (serverMap.size() == 1) {
				serverList.add(serverMeta);
				serverList.add(serverMeta);
			}
		}
	}

	/** app instance start */
	public void start() {
		if ((metaHolder.getFeature1() & Feature1.clientFeature_needKeepConnection) > 0) {
			needKeepConnection = true;
		} else {
			needKeepConnection = false;
		}

		if (needKeepConnection) {
			makeConnectionInHeartbeatThread();
		}
	}

	@Override
	/**
	 * appp instance close
	 */
	public void close() {
		clossResource();
	}

	/**
	 * close resource
	 */
	public synchronized void clossResource() {
		if (heartbeatRunner != null) {
			try {
				heartbeatRunner.close();
			} catch (Throwable ex) {
				log.error(ex.getMessage(), ex);
			}
		}

		if (channel != null) {
			try {
				channel.close();
			} catch (Throwable ex) {
				log.error(ex.getMessage(), ex);
			} finally {
				channel = null;
			}
		}
	}

	private void makeConnectionInHeartbeatThread() {
		if (heartbeatIsStarted) {
			return;
		}
		boolean success = heartbeatIsStarting.compareAndSet(false, true);
		if (!success) {
			return;
		}

		if (heartbeatRunner != null) {
			try {
				heartbeatRunner.close();
			} catch (Throwable ex) {
				log.debug(ex.getMessage(), ex);
			}
		}

		try {
			heartbeatRunner = new HeartbeatThread();
			heartbeatRunner.start();
		} finally {
			heartbeatIsStarting.set(false);
		}
	}

	private void makeConnectionInCallerThread() {
		if (isChannelActive()) {
			return;
		}
		metaHolder.getRequestPool().clear();

		boolean success = isReconnecting.compareAndSet(false, true);
		if (!success) {
			return;
		}
		try {
			reconnect();
		} finally {
			isReconnecting.set(false);
		}

	}

	private boolean isChannelActive() {
		if (channel != null && (channel.isActive() || channel.isOpen())) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isRpcWithServerOk() {
		return isChannelActive(); // FIXME too simple
	}

	private void reconnect() {
		Channel localChannel = null;
		Random rand = new Random();
		for (int i = 0; i < serverList.size(); i++) {
			int index = rand.nextInt(serverList.size());
			localChannel = connect(serverList.get(index));
			if (localChannel != null) {
				channel = localChannel;
				return;
			}
		}
		for (ServerMeta serverConfig : serverList) {
			localChannel = connect(serverConfig);
			if (localChannel != null) {
				channel = localChannel;
				return;
			}
		}
	}

	private Channel connect(ServerMeta serverConfig) {
		final LengthFieldPrepender customLenFrameEncoder = new LengthFieldPrepender(4, true);
		final Wamp2ByteBufEncoder wamp2ByteBufEncoder = new Wamp2ByteBufEncoder();
		final Byte2WampDecoder byte2WampDecoder = new Byte2WampDecoder();
		final WampJsonArrayHandler msgHandler = new WampJsonArrayHandler(metaHolder);
		final DisconnectDetectHandler disconnectDetectHandler = new DisconnectDetectHandler(bossThreadEventQueue);

		Bootstrap clientBoot = new Bootstrap();
		clientBoot.group(new NioEventLoopGroup())
				.channel(NioSocketChannel.class)
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 8000) // IMPORTANT
				.handler(new ChannelInitializer<Channel>() {
					@Override
					protected void initChannel(Channel ch) throws Exception {
						ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO))
								.addLast(disconnectDetectHandler)
								.addLast(customLenFrameEncoder)// encoder顺序要保证
								.addLast(wamp2ByteBufEncoder)
								.addLast(new LengthFieldBasedFrameDecoder(1000000, 0, 4, -4, 4))
								.addLast(byte2WampDecoder)
								.addLast(msgHandler);
					}
				});

		try {
			ChannelFuture future = clientBoot.connect(serverConfig.getServerName(), serverConfig.getPort());
			boolean isSuccess = future.awaitUninterruptibly(8, TimeUnit.SECONDS);
			if (isSuccess) {
				serverConfig.addConnectSuccessCount();
				return future.channel();
			} else {
				serverConfig.addConnectErrorCount();
				future.cancel(true);
				return null;
			}
		} catch (Throwable ex) {
			log.error(serverConfig.toString(), ex);
			serverConfig.addConnectErrorCount();
			return null;
		}
	}

	public void sendRpcAsync(String requestUrl, Object[] args, Class<?> returnType) {
		this.sendRpc(requestUrl, args, returnType, 0);
	}

	/**
	 * 
	 * @param requestUrl
	 * @param args
	 * @param returnType
	 * @param timeoutInMs
	 * 	if(timeoutInMs == 0) then send async
	 * @return
	 */
	public Object sendRpc(String requestUrl, Object[] args, Class<?> returnType, long timeoutInMs) {
		if (needKeepConnection) {
			makeConnectionInHeartbeatThread();
		} else {
			makeConnectionInCallerThread();
		}
		CallCommand callCmd = new CallCommand();
		callCmd.setProcedureUri(requestUrl);
		callCmd.setArgs(args);
		CallResultFuture future = new CallResultFuture(returnType);
		metaHolder.getRequestPool().put(callCmd.getRequestId(), future);
		try {
			boolean sended = false;
			for (int i = 0; i < 150; i++) {
				if (channel == null || !channel.isActive()) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						log.error("{msg:'InterruptedException will be ignore'}", e);
					}
					continue;
				} else {
					if (timeoutInMs > 0) {
						channel.writeAndFlush(callCmd);
					} else {
						channel.write(callCmd);
					}
					sended = true;
					break;
				}
			}
			if(timeoutInMs == 0){
				return null;
			}
			if (sended) {
				Object result = future.get(timeoutInMs); // FIXME return void
				return result;
			} else {
				throw new TimeoutException("timeout exceed 300000ms");// TODO
			}
		} finally {
			metaHolder.getRequestPool().remove(callCmd.getRequestId());
		}
	}

	class HeartbeatThread extends Thread {
		private volatile boolean toStop = false;

		HeartbeatThread() {
			super("MappingPackageClient-Heartbeat");
			heartbeatIsStarted = true;
			setDaemon(true);
		}

		public void close() {
			toStop = true;
			bossThreadEventQueue.offer(new BossThreadEvent(BossThreadEventType.closeBossThread));
		}

		@Override
		public void run() {
			makeConnectionInCallerThread();
			while (true) {
				try {
					BossThreadEvent event = bossThreadEventQueue.poll(500, TimeUnit.MILLISECONDS);
					if (isInterrupted()) {
						return;
					}
					if (toStop) {
						return;
					}

					if (event == null) {// poll() timeout 500ms
						makeConnectionInCallerThread();
						continue;
					}

					if (event.getEventType() == BossThreadEventType.channelDisconnected) {
						makeConnectionInCallerThread();
						continue;
					}
				} catch (InterruptedException ex) {
					break;
				} catch (Throwable ex) {
					log.error(ex.getMessage(), ex);
					continue;
				}
			}
		}

	}
}
