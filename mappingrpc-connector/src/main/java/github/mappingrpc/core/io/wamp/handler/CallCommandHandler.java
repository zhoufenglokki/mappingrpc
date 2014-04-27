package github.mappingrpc.core.io.wamp.handler;

import github.mappingrpc.core.io.wamp.domain.command.CallCommand;
import github.mappingrpc.core.io.wamp.domain.command.ExceptionErrorCommand;
import github.mappingrpc.core.io.wamp.domain.command.ResultCommand;
import github.mappingrpc.core.metadata.MetaHolder;
import github.mappingrpc.core.metadata.ProviderMeta;
import io.netty.channel.ChannelHandlerContext;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class CallCommandHandler {
	static Logger log = LoggerFactory.getLogger(CallCommandHandler.class);

	public static void processCommand(MetaHolder metaHolder, ChannelHandlerContext channelCtx, JSONArray jsonArray) {
		CallCommand callCmd = new CallCommand();
		assert jsonArray.size() >= 5;
		int i = 1;
		callCmd.setRequestId(jsonArray.getLongValue(i++));
		callCmd.setOptions(jsonArray.getString(i++));
		callCmd.setProcedureUri(jsonArray.getString(i++));
		ProviderMeta providerMeta = metaHolder.getProviderHolder().get(callCmd.getProcedureUri());
		Method method = providerMeta.getMethod();
		Object[] args = JSONObject.parseArray(jsonArray.getString(i++), method.getGenericParameterTypes()).toArray();

		try {
			Object result = method.invoke(providerMeta.getServiceImpl(), args);
			ResultCommand resultCmd = new ResultCommand(callCmd.getRequestId(), result);
			channelCtx.writeAndFlush(resultCmd);
		} catch (Throwable e) {
			log.error("{procedureUri:'" + callCmd.getProcedureUri() + "'}", e);
			ExceptionErrorCommand errCmd = new ExceptionErrorCommand(callCmd.getRequestId(), e);//需要将e堆栈展开成字符串
			channelCtx.writeAndFlush(errCmd);
		}

	}
}
