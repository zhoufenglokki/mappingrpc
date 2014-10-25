package github.mappingrpc.core.io.wamp.handler;

import github.mappingrpc.core.metadata.MetaHolder;
import github.mappingrpc.core.rpc.CallResultFuture;
import io.netty.channel.ChannelHandlerContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;

public class ResultCommandHandler {
	static Logger log = LoggerFactory.getLogger(ResultCommandHandler.class);

	public static void processCommand(MetaHolder metaHolder, ChannelHandlerContext channelCtx, JSONArray jsonArray) {
		long requestId = jsonArray.getLongValue(1);
		CallResultFuture callResult = metaHolder.getRequestPool().get(requestId);
		if (callResult == null) {
			log.error("{msg:'receive timeout result, maybe server method too slow', requestId:" + requestId + "}");
			return;
		}
		callResult.setDetail(jsonArray.getJSONObject(2));

		if(callResult.getReturnType() == null){
			callResult.returnWithVoid();
		}else{
			Object result = jsonArray.getJSONArray(3).getObject(0, callResult.getReturnType());
			callResult.putResultAndReturn(result);
		}
	}
}
