package github.mappingrpc.core.rpc;

public class CallResultFuture {
	private Object result;
	Object lock = new Object();

	private Class<?> returnType;

	public CallResultFuture(Class<?> returnType) {
		this.returnType = returnType;
	}

	public Object get(long timeoutInMs) {
		synchronized (lock) {
			try {
				lock.wait(timeoutInMs);
			} catch (InterruptedException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}

		if (result == null) {
			throw new RuntimeException("timeout");
		}
		if (result instanceof Throwable) {
			Throwable e = (Throwable) result;
			throw new RuntimeException(e);
		}
		return result;
	}

	public void put(Object result) {
		this.result = result;
		synchronized (lock) {
			lock.notifyAll();
		}
	}

	public Class<?> getReturnType() {
		return returnType;
	}

	public void setReturnType(Class<?> returnType) {
		this.returnType = returnType;
	}

}
