package github.mappingrpc.core.io.wamp.domain.command;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import github.mappingrpc.core.io.wamp.constant.MsgTypeConstant;

public class ResultCommand implements WampCommandBase {

	private int msgType = MsgTypeConstant.result;
	private long requestId = 0;
	private JSONObject details = new JSONObject();
	private Object[] yieldResult = new Object[1];

	public ResultCommand() {

	}

	public ResultCommand(long requestId, Object result) {
		this.requestId = requestId;
		yieldResult[0] = result;
	}

	public int getMsgType() {
		return msgType;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}

	public long getRequestId() {
		return requestId;
	}

	public void setRequestId(long requestId) {
		this.requestId = requestId;
	}

	public JSONObject getDetails() {
		return details;
	}

	public void setDetails(JSONObject details) {
		this.details = details;
	}

	public Object[] getYieldResult() {
		return yieldResult;
	}

	public void setYieldResult(String[] yieldResult) {
		this.yieldResult = yieldResult;
	}

	@Override
	public Object[] fieldToArray() {
		return new Object[] { msgType, requestId, details, yieldResult };
	}

	@Override
	public String toCommandJson() {
		return JSONObject.toJSONString(fieldToArray(), SerializerFeature.WriteClassName);
	}

}
