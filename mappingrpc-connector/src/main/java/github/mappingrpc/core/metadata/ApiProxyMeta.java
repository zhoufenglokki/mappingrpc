package github.mappingrpc.core.metadata;

public class ApiProxyMeta {
	private String requestUrl;
	private Class<?> returnType;
	private Class<?>[] parameterTypes;
	private boolean paramTypeGeneric;

	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	public Class<?> getReturnType() {
		return returnType;
	}

	public void setReturnType(Class<?> returnType) {
		this.returnType = returnType;
	}

	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public boolean isParamTypeGeneric() {
		return paramTypeGeneric;
	}

	public void setParamTypeGeneric(boolean paramTypeGeneric) {
		this.paramTypeGeneric = paramTypeGeneric;
	}

}
