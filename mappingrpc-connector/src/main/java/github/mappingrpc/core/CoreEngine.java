package github.mappingrpc.core;

import github.mappingrpc.api.annotation.RequestMapping;
import github.mappingrpc.api.callback.ClientSideConnectedBizListener;
import github.mappingrpc.api.constant.SiteConfigConstant;
import github.mappingrpc.core.io.custompackage.MappingPackageClient;
import github.mappingrpc.core.io.custompackage.MappingPackageServer;
import github.mappingrpc.core.metadata.MetaHolder;
import github.mappingrpc.core.metadata.ProviderMeta;
import github.mappingrpc.core.rpc.ServiceInvocationHandler;

import java.io.Closeable;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;

public class CoreEngine implements BeanNameAware, Closeable {
	static Logger log = LoggerFactory.getLogger(CoreEngine.class);
	
	private boolean isListenEngine = false;
	private long feature1 = 0;
	private String listenIp; // TODO
	private short listenPort = 6200;
	private Map<String, String> serverList;
	private Map<String, String> siteConfig = new ConcurrentHashMap<String, String>();
	private String beanName;
	private String cookieStoreManagerClass = "github.mappingrpc.api.clientside.manager.CookieStoreManager";
	private boolean needStoreCookieToDisk = false;

	MetaHolder metaHolder = new MetaHolder();
	MappingPackageServer listenServer;
	MappingPackageClient connectServer;

	public CoreEngine(boolean isListenEngine) {
		this.isListenEngine = isListenEngine;
	}

	public void start() {
		metaHolder.setFeature1(feature1);
		
		ArrayBlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(300);
		ExecutorService threadPool = new ThreadPoolExecutor(4, 80, 60, TimeUnit.SECONDS, workQueue);// TODO
																									// 加上rejectHandle
		metaHolder.setThreadPool(threadPool);

		if (isListenEngine) {
			listenServer = new MappingPackageServer(metaHolder, listenPort);
			listenServer.start();
		} else {
			siteConfig.put(SiteConfigConstant.client_connectionName, this.beanName);

			connectServer = new MappingPackageClient(metaHolder, serverList);
			connectServer.setSiteConfig(siteConfig);
			connectServer.start();
			serverList = null;
		}
	}

	public Object createConsumerProxy(String serviceInterface) {
		ServiceInvocationHandler proxyHandler = metaHolder.getClientProxyHolder().get(serviceInterface);
		if (proxyHandler == null) {// FIXME
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

	public void setProviderList(List<Object> serviceImplList) {
		for (Object serviceImpl : serviceImplList) {
			createProvider(serviceImpl);
		}
	}

	public void close() {
		if (connectServer != null) {
			connectServer.close();
		}
		if(listenServer != null){
			listenServer.close();
		}
	}
	
	public boolean isRpcWithServerOk(){
		return connectServer.isRpcWithServerOk();
	} 

	public void setListenPort(short listenPort) {
		this.listenPort = listenPort;
	}

	public Map<String, String> getServerList() {
		return serverList;
	}

	public void setServerList(Map<String, String> serverList) {
		this.serverList = serverList;
	}

	public void setFeature1(long connectionFeature) {
		this.feature1 = connectionFeature;
	}

	@Override
	public void setBeanName(String name) {
		this.beanName = name;
	}
	
	public void setSiteConfig(Map<String, String> siteConfig){
		this.siteConfig = siteConfig;
	}

	public void setClientSideConnectedBizListener(ClientSideConnectedBizListener listener){
		
	}
}
