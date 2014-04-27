package github.mappingrpc.core.io.custompackage;

import github.mappingrpc.core.io.wamp.domain.command.CallCommand;
import github.mappingrpc.core.metadata.MetaHolder;
import github.mappingrpc.core.rpc.CallResultFuture;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MappingPackageClient {
	static Logger log = LoggerFactory.getLogger(MappingPackageClient.class);

	private MetaHolder metaHolder;
	private String serverName;
	private short serverPort = 6200;

	EventLoopGroup workerEventLoop = new NioEventLoopGroup();
	Channel channel;

	public MappingPackageClient(MetaHolder metaHolder, String serverName, short serverPort) {
		this.metaHolder = metaHolder;
		this.serverName = serverName;
		this.serverPort = serverPort;
	}

	public void start() {
		Bootstrap nettyBoot = new Bootstrap();
		final LengthFieldPrepender customLenFrameEncoder = new LengthFieldPrepender(4, true);
		final Wamp2ByteBufEncoder wamp2ByteBufEncoder = new Wamp2ByteBufEncoder();
		final Byte2WampDecoder byte2WampDecoder = new Byte2WampDecoder();
		final WampJsonArrayHandler msgHandler = new WampJsonArrayHandler(metaHolder);

		nettyBoot.group(workerEventLoop)
				.channel(NioSocketChannel.class)
				.remoteAddress(serverName, serverPort)
				.handler(new ChannelInitializer<Channel>() {
					@Override
					protected void initChannel(Channel ch) throws Exception {
						ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO))
								.addLast(customLenFrameEncoder)// encoder顺序要保证
								.addLast(wamp2ByteBufEncoder)
								.addLast(new LengthFieldBasedFrameDecoder(1000000, 0, 4, -4, 4))
								.addLast(byte2WampDecoder)
								.addLast(msgHandler);
					}
				});
		try {
			channel = nettyBoot.connect().sync().channel();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public Object sendRpc(String requestUrl, Object[] args, Class<?> returnType) {
		CallCommand callCmd = new CallCommand();
		callCmd.setProcedureUri(requestUrl);
		callCmd.setArgs(args);
		CallResultFuture future = new CallResultFuture(returnType);
		metaHolder.getRequestPool().put(callCmd.getRequestId(), future);
		channel.writeAndFlush(callCmd);
		return future.get(300000);
	}
}
