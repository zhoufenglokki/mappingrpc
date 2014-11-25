package github.mappingrpc.core.io.wamp.handler;

import github.mappingrpc.api.clientside.domain.Cookie;
import github.mappingrpc.api.threadmodel.ServerCookieManager;
import github.mappingrpc.core.io.wamp.domain.command.CallCommand;
import github.mappingrpc.core.io.wamp.domain.command.ExceptionErrorCommand;
import github.mappingrpc.core.io.wamp.domain.command.ResultCommand;
import github.mappingrpc.core.metadata.MetaHolder;
import github.mappingrpc.core.metadata.ProviderMeta;
import io.netty.channel.ChannelHandlerContext;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class CallCommandHandler {
	static Logger log = LoggerFactory.getLogger(CallCommandHandler.class);

	public static void processCommand(MetaHolder metaHolder, ChannelHandlerContext channelCtx, JSONArray jsonArray) {
		CallCommand callCmd = new CallCommand();

		try {
			callCmd.setRequestId(jsonArray.getLongValue(1));
			// System.err.println("2:" + jsonArray.getString(2));
			callCmd.setOptions(jsonArray.getJSONObject(2));
			callCmd.setProcedureUri(jsonArray.getString(3));
			ProviderMeta providerMeta = metaHolder.getProviderHolder().get(callCmd.getProcedureUri());
			Method method = providerMeta.getMethod();
			Object[] args = JSONObject.parseArray(jsonArray.getString(4), method.getGenericParameterTypes()).toArray();

			JSONArray cookieArray = callCmd.getOptions().getJSONArray("Cookie");
			if (cookieArray != null) {
				Map<String, Cookie> receiveCookieMap = new ConcurrentHashMap<>(8);
				for (int i = 0; i < cookieArray.size(); i++) {
					Cookie cookie = cookieArray.getObject(i, Cookie.class);
					receiveCookieMap.put(cookie.getName(), cookie);
				}
				ServerCookieManager.setReceiveCookieMap(receiveCookieMap);
			} else {
				ServerCookieManager.setReceiveCookieMap(new ConcurrentHashMap<>(1));
			}

			Object result = method.invoke(providerMeta.getServiceImpl(), args);

			ServerCookieManager.clearReceiveCookieMap();

			ResultCommand resultCmd = new ResultCommand(callCmd.getRequestId(), result);
			Map<String, Cookie> setCookieMap = ServerCookieManager.getSetCookieMap();
			if (setCookieMap != null && setCookieMap.size() > 0) {
				resultCmd.getDetails().put("Set-Cookie", setCookieMap.values().toArray());
				ServerCookieManager.clearSetCookieMap();
			}

			channelCtx.writeAndFlush(resultCmd);
		} catch (Throwable e) {
			log.error("{procedureUri:'" + callCmd.getProcedureUri() + "'}", e);
			ExceptionErrorCommand errCmd = new ExceptionErrorCommand(callCmd.getRequestId(), e);// 需要将e堆栈展开成字符串
			channelCtx.writeAndFlush(errCmd);
		}

	}
}
