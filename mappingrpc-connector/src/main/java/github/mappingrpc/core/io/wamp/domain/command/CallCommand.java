package github.mappingrpc.core.io.wamp.domain.command;

import github.mappingrpc.core.io.wamp.constant.MsgTypeConstant;

import java.util.concurrent.atomic.AtomicLong;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class CallCommand implements WampCommandBase {
	private int msgType = MsgTypeConstant.call;
	private long requestId = 1;
	private JSONObject options = new JSONObject();
	private String procedureUri;
	private Object[] args;
	
	static AtomicLong requestIdPool = new AtomicLong(1);
	
	public CallCommand(){
		requestId = requestIdPool.incrementAndGet();
	}

	@Override
	public int getMsgType() {
		return msgType;
	}

	public long getRequestId() {
		return requestId;
	}

	public void setRequestId(long requestId) {
		this.requestId = requestId;
	}

	public JSONObject getOptions() {
		return options;
	}

	public void setOptions(JSONObject options) {
		this.options = options;
	}

	public String getProcedureUri() {
		return procedureUri;
	}

	public void setProcedureUri(String procedureUrl) {
		this.procedureUri = procedureUrl;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	@Override
	public Object[] fieldToArray() {
		return new Object[]{msgType, requestId, options, procedureUri, args};
	}

	@Override
	public String toCommandJson() {
		return JSONObject.toJSONString(fieldToArray());
	}

}
