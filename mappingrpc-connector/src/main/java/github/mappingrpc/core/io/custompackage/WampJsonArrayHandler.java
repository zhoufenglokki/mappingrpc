package github.mappingrpc.core.io.custompackage;

import github.mappingrpc.core.io.wamp.handler.WampCommandBaseHandler;
import github.mappingrpc.core.metadata.MetaHolder;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import com.alibaba.fastjson.JSONArray;

@Sharable
public class WampJsonArrayHandler extends ChannelInboundHandlerAdapter {
	private MetaHolder metaHolder;

	public WampJsonArrayHandler(MetaHolder metaHolder) {
		this.metaHolder = metaHolder;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (!(msg instanceof JSONArray)) {
			super.channelRead(ctx, msg);
		}
		JSONArray jsonArray = (JSONArray) msg;
		WampCommandBaseHandler commandHandler = new WampCommandBaseHandler(metaHolder, ctx, jsonArray);
		metaHolder.getThreadPool().submit(commandHandler);
	}
}
