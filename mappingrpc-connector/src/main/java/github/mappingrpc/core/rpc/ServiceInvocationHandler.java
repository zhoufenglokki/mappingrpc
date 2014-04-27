package github.mappingrpc.core.rpc;

import github.mappingrpc.api.annotation.RequestMapping;
import github.mappingrpc.core.io.custompackage.MappingPackageClient;
import github.mappingrpc.core.metadata.ApiProxyMeta;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceInvocationHandler implements InvocationHandler {
	Logger log = LoggerFactory.getLogger(ServiceInvocationHandler.class);

	private MappingPackageClient rpcClient;
	Map<Method, ApiProxyMeta> apiHolder = new HashMap<>();

	public ServiceInvocationHandler(MappingPackageClient rpcClient) {
		this.rpcClient = rpcClient;
	}

	public Object generateProxy(String serviceInterface) {
		Class<?> clazz;
		try {
			clazz = Class.forName(serviceInterface);// FIXME
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		for (Method method : clazz.getMethods()) {
			RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
			if (requestMapping != null) {
				ApiProxyMeta apiMeta = new ApiProxyMeta();
				apiMeta.setRequestUrl(requestMapping.value());
				apiMeta.setParameterTypes(method.getParameterTypes());
				apiMeta.setReturnType(method.getReturnType());
				apiHolder.put(method, apiMeta);
			}
		}

		Object proxy = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz }, this);
		return proxy;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// TODO 过滤掉hashCode()/toString()/equals等本地方法

		ApiProxyMeta meta = apiHolder.get(method);
		return rpcClient.sendRpc(meta.getRequestUrl(), args, meta.getReturnType());
	}

}
