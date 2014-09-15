package github.mappingrpc.core.io.wamp.handler;

import github.mappingrpc.api.serverside.domain.UserSessionMapping;
import github.mappingrpc.core.io.wamp.domain.command.WellcomeCommand;
import github.mappingrpc.core.metadata.MetaHolder;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;

public class HelloCommandHandler {
	static Logger log = LoggerFactory.getLogger(HelloCommandHandler.class);

	static Map<String, UserSessionMapping> sessionPool = new ConcurrentHashMap<>(300);

	public static void processCommand(MetaHolder metaHolder, ChannelHandlerContext channelCtx, JSONArray jsonArray) {
		String realm = jsonArray.getString(1);
		if (realm.equals("newSession")) {
			UserSessionMapping userSession = new UserSessionMapping();
			String sessionId = UUID.randomUUID().toString();
			if (sessionPool.putIfAbsent(sessionId, userSession) != null) {
				log.warn("{msg:'uuid碰撞'}");
				channelCtx.close();
			}
			WellcomeCommand wellcome = new WellcomeCommand();
			wellcome.setSession(sessionId);
			channelCtx.writeAndFlush(wellcome);
			// TODO call sessionListener
		} else {
			String sessionId = realm;
			WellcomeCommand wellcome = new WellcomeCommand();
			wellcome.setSession(sessionId);
			channelCtx.writeAndFlush(wellcome);
			channelCtx.writeAndFlush(wellcome);

		}
	}
}
