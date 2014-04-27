package github.mappingrpc.core.io.custompackage;

import static io.netty.buffer.Unpooled.wrappedBuffer;
import github.mappingrpc.core.io.wamp.domain.command.WampCommandBase;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

@Sharable
public class Wamp2ByteBufEncoder extends MessageToMessageEncoder<WampCommandBase> {

	@Override
	protected void encode(ChannelHandlerContext ctx, WampCommandBase msg, List<Object> out) throws Exception {
		out.add(wrappedBuffer(msg.toCommandJson().getBytes("UTF-8")));
	}

}
