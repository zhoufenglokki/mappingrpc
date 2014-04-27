package github.mappingrpc.core.metadata;

import github.mappingrpc.core.rpc.CallResultFuture;
import github.mappingrpc.core.rpc.ServiceInvocationHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;


public class MetaHolder {
	private Map<String, ProviderMeta> providerHolder = new HashMap<>();
	private Map<String, ServiceInvocationHandler> clientProxyHolder = new HashMap<>();
	private Map<Long, CallResultFuture> requestPool = new ConcurrentHashMap<>();
	private ExecutorService threadPool;
	private ExecutorService sysThreadPool;// for QOS

	public Map<String, ProviderMeta> getProviderHolder() {
		return providerHolder;
	}

	public Map<String, ServiceInvocationHandler> getClientProxyHolder() {
		return clientProxyHolder;
	}

	public Map<Long, CallResultFuture> getRequestPool() {
		return requestPool;
	}

	public void setRequestPool(Map<Long, CallResultFuture> requestPool) {
		this.requestPool = requestPool;
	}

	public ExecutorService getThreadPool() {
		return threadPool;
	}

	public void setThreadPool(ExecutorService threadPool) {
		this.threadPool = threadPool;
	}
}
