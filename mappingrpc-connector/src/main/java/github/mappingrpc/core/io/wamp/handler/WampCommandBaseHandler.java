package github.mappingrpc.core.io.wamp.handler;

import github.mappingrpc.core.io.wamp.constant.MsgTypeConstant;
import github.mappingrpc.core.metadata.MetaHolder;
import io.netty.channel.ChannelHandlerContext;

import com.alibaba.fastjson.JSONArray;

public class WampCommandBaseHandler implements Runnable {
	private MetaHolder metaHolder;
	private JSONArray jsonArray;
	private ChannelHandlerContext channelCtx;
	
	public WampCommandBaseHandler(MetaHolder metaHolder, ChannelHandlerContext channelCtx, JSONArray jsonArray) {
		this.metaHolder = metaHolder;
		this.channelCtx = channelCtx;
		this.jsonArray = jsonArray;
	}

	@Override
	public void run() {
		int commandType = jsonArray.getIntValue(0);
		switch (commandType) {
		case MsgTypeConstant.call:
			CallCommandHandler.processCommand(metaHolder, channelCtx, jsonArray);
			break;
		case MsgTypeConstant.result:
			ResultCommandHandler.processCommand(metaHolder, channelCtx, jsonArray);
			break;
		case MsgTypeConstant.error:
			break;
		}
	}

}
