package github.mappingrpc.core.rpc;

import github.mappingrpc.api.exception.TimeoutException;

import com.alibaba.fastjson.JSONObject;

public class CallResultFuture {
	private Object result;
	private JSONObject detail;
	Object lock = new Object();

	private Class<?> returnType;

	public CallResultFuture(Class<?> returnType) {
		this.returnType = returnType;
	}

	public void waitReturn(long timeoutInMs) {
		synchronized (lock) {
			try {
				lock.wait(timeoutInMs);
			} catch (InterruptedException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}

		if (result == null && returnType != null) {
			throw new TimeoutException("{timeoutInMs:" + timeoutInMs + "}");
		}
		if (result instanceof Throwable) {
			Throwable e = (Throwable) result;
			throw new RuntimeException(e);
		}
	}

	public void putResultAndReturn(Object result) {
		this.result = result;
		synchronized (lock) {
			lock.notifyAll();
		}
	}
	
	public void returnWithVoid(){
		synchronized (lock) {
			lock.notifyAll();
		}
	}
	
	@Deprecated
	/**
	 * Deprecated: not in use
	 * @param detail
	 * @return
	 */
	public CallResultFuture putDetail(JSONObject detail){
		this.detail = detail;
		return this;
	}

	public Object getResult(){
		return result;
	}

	public JSONObject getDetail() {
		return detail;
	}

	public void setDetail(JSONObject detail) {
		this.detail = detail;
	}

	public Class<?> getReturnType() {
		return returnType;
	}

	public void setReturnType(Class<?> returnType) {
		this.returnType = returnType;
	}

}
