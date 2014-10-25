package github.mappingrpc.core.metadata;

import java.lang.reflect.Method;

public class ProviderMeta {
	private String mapping;
	private Object serviceImpl;
	private Method method;
	private boolean returnTypeGeneric;

	public String getMapping() {
		return mapping;
	}

	public void setMapping(String mapping) {
		this.mapping = mapping;
	}

	public Object getServiceImpl() {
		return serviceImpl;
	}

	public void setServiceImpl(Object serviceImpl) {
		this.serviceImpl = serviceImpl;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public boolean isReturnTypeGeneric() {
		return returnTypeGeneric;
	}

	public void setReturnTypeGeneric(boolean returnTypeGeneric) {
		this.returnTypeGeneric = returnTypeGeneric;
	}
}
