package github.mappingrpc.core.io.wamp.handler;

import github.mappingrpc.core.io.wamp.domain.command.ResultCommand;
import github.mappingrpc.core.metadata.MetaHolder;
import github.mappingrpc.core.rpc.CallResultFuture;
import io.netty.channel.ChannelHandlerContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;

public class ResultCommandHandler {
	static Logger log = LoggerFactory.getLogger(ResultCommandHandler.class);

	public static void processCommand(MetaHolder metaHolder, ChannelHandlerContext channelCtx, JSONArray jsonArray) {
		ResultCommand resultCmd = new ResultCommand();
		assert jsonArray.size() >= 4;
		int i = 1;
		resultCmd.setRequestId(jsonArray.getLongValue(i++));
		CallResultFuture future = metaHolder.getRequestPool().get(resultCmd.getRequestId());
		if (future == null) {
			log.error("{msg:'receive timeout result, maybe server method too slow', requestId:" + resultCmd.getRequestId() + "}");
			return;
		}
		i++;// skip resultCmd.setDetails(jsonArray.getString(i++));
		// JSONArray resultArray = jsonArray.getJSONArray(i++);
		Object result = jsonArray.getJSONArray(i++).getObject(0, future.getReturnType());
		future.put(result);
	}
}
