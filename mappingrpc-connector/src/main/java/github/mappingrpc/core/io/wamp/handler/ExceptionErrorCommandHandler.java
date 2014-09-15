package github.mappingrpc.core.io.wamp.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import github.mappingrpc.core.metadata.MetaHolder;
import io.netty.channel.ChannelHandlerContext;

import com.alibaba.fastjson.JSONArray;

public class ExceptionErrorCommandHandler {
	static Logger log = LoggerFactory.getLogger(ExceptionErrorCommandHandler.class);

	public static void processCommand(MetaHolder metaHolder, ChannelHandlerContext channelCtx, JSONArray jsonArray) {
	}
}
