package github.mappingrpc.api.exception;

public class TimeoutException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public TimeoutException(String msg){
		super(msg);
	}
}
