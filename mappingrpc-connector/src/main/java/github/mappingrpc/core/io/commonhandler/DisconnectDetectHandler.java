package github.mappingrpc.core.io.commonhandler;

import github.mappingrpc.core.constant.ClientDaemonThreadEventType;
import github.mappingrpc.core.event.ClientDaemonThreadEvent;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.BlockingQueue;

@Sharable
public class DisconnectDetectHandler extends ChannelDuplexHandler {
	private BlockingQueue<ClientDaemonThreadEvent> nettyEventToOuter;

	public DisconnectDetectHandler(BlockingQueue<ClientDaemonThreadEvent> nettyEventToOuter) {
		this.nettyEventToOuter = nettyEventToOuter;
	}

	/*
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		nettyEventToOuter.add(new BossThreadEvent(BossThreadEventType.channelConnected));
	}*/

	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		nettyEventToOuter.add(new ClientDaemonThreadEvent(ClientDaemonThreadEventType.channelDisconnected));
		super.channelInactive(ctx);
	}

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		ctx.close();
		nettyEventToOuter.add(new ClientDaemonThreadEvent(ClientDaemonThreadEventType.channelDisconnected));
	}
}
