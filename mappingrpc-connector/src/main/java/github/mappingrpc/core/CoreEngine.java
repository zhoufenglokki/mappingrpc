package github.mappingrpc.core;

import github.mappingrpc.api.annotation.RequestMapping;
import github.mappingrpc.api.constant.ConnectionFeature;
import github.mappingrpc.core.io.custompackage.MappingPackageClient;
import github.mappingrpc.core.io.custompackage.MappingPackageServer;
import github.mappingrpc.core.metadata.MetaHolder;
import github.mappingrpc.core.metadata.ProviderMeta;
import github.mappingrpc.core.rpc.ServiceInvocationHandler;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CoreEngine {
	private boolean isListenEngine = false;
	private int connectionFeature = 0;
	private String serverName;
	private short serverPort = 6200;

	MetaHolder metaHolder = new MetaHolder();
	MappingPackageServer listenServer;
	MappingPackageClient connectServer;

	public CoreEngine(int connectionFeature, boolean isListenEngine) {
		this.connectionFeature = connectionFeature;
		this.isListenEngine = isListenEngine;
	}
	
	public void start() {
		ArrayBlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(300);
		ExecutorService threadPool = new ThreadPoolExecutor(4, 80, 60, TimeUnit.SECONDS, workQueue);// TODO 加上rejectHandle
		metaHolder.setThreadPool(threadPool);

		if (isListenEngine) {
			listenServer = new MappingPackageServer(metaHolder, serverPort);
			listenServer.start();
		} else {
			connectServer = new MappingPackageClient(metaHolder, serverName, serverPort);
			if ((connectionFeature & ConnectionFeature.needConnectToServerAtStart) > 0) {
				connectServer.start();
			}
		}
	}

	public Object createConsumerProxy(String serviceInterface) {
		ServiceInvocationHandler proxyHandler = metaHolder.getClientProxyHolder().get(serviceInterface);
		if(proxyHandler == null){// FIXME
			proxyHandler = new ServiceInvocationHandler(connectServer);
			metaHolder.getClientProxyHolder().put(serviceInterface, proxyHandler);
		}

		return proxyHandler.generateProxy(serviceInterface);
	}

	/**
	 * 可以并需要运行在start()前
	 * 
	 * @param serviceImpl
	 */
	public void createProvider(Object serviceImpl) {
		Method[] methodList = serviceImpl.getClass().getMethods();
		for (Method method : methodList) {
			RequestMapping mapping = method.getAnnotation(RequestMapping.class);
			if (mapping != null) {
				ProviderMeta meta = new ProviderMeta();
				meta.setMapping(mapping.value());
				meta.setMethod(method);
				meta.setServiceImpl(serviceImpl);
				metaHolder.getProviderHolder().put(mapping.value(), meta);
			}
		}
	}
	
	public void setProviderList(List<Object> serviceImplList){
		for(Object serviceImpl : serviceImplList){
			createProvider(serviceImpl);
		}
	}

	public void close() {

	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public short getServerPort() {
		return serverPort;
	}

	public void setServerPort(short serverPort) {
		this.serverPort = serverPort;
	}
}
