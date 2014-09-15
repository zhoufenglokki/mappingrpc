package github.mappingrpc.core.io.wamp.domain.command;

import github.mappingrpc.core.io.wamp.constant.MsgTypeConstant;

public class HelloCommand {
	private int msgType = MsgTypeConstant.hello;
	private String realm = "newSession";

	public int getMsgType() {
		return msgType;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}

	public String getRealm() {
		return realm;
	}

	public void setRealm(String realm) {
		this.realm = realm;
	}

}
