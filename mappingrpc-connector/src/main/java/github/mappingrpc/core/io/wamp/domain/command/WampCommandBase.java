package github.mappingrpc.core.io.wamp.domain.command;

import com.alibaba.fastjson.JSONObject;

public interface WampCommandBase {
	public int getMsgType();
	
	public Object[] fieldToArray();
	
	public default String toCommandJson(){
		return JSONObject.toJSONString(fieldToArray());
	}
}
