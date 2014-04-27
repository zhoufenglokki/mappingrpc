package github.mappingrpc.core.io.wamp.domain.command;

public interface WampCommandBase {
	public int getMsgType();
	
	public Object[] fieldToArray();
	
	public String toCommandJson();
}
