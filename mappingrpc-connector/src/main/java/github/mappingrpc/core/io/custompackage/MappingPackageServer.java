package github.mappingrpc.core.io.custompackage;

import github.mappingrpc.core.metadata.MetaHolder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.io.Closeable;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MappingPackageServer implements Closeable {
	static Logger log = LoggerFactory.getLogger(MappingPackageServer.class);

	private MetaHolder metaHolder;
	private short serverPort;

	EventLoopGroup bossEventLoop = new NioEventLoopGroup();
	EventLoopGroup workerEventLoop = new NioEventLoopGroup();

	public MappingPackageServer(MetaHolder metaHolder, short serverPort) {
		this.metaHolder = metaHolder;
		this.serverPort = serverPort;
	}

	public void start() {
		ServerBootstrap nettyBoot = new ServerBootstrap();
		final LengthFieldPrepender customLenFrameEncoder = new LengthFieldPrepender(4, true);
		final Wamp2ByteBufEncoder wamp2ByteBufEncoder = new Wamp2ByteBufEncoder();
		final Byte2WampDecoder byte2WampDecoder = new Byte2WampDecoder();
		final WampJsonArrayHandler msgHandler = new WampJsonArrayHandler(metaHolder);

		nettyBoot.group(bossEventLoop, workerEventLoop)
				.channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_BACKLOG, 100)
				.handler(new LoggingHandler(LogLevel.INFO))
				.childHandler(new ChannelInitializer<Channel>() {
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
			ChannelFuture channelFuture = nettyBoot.bind(serverPort).sync();
		} catch (InterruptedException ex) {
			log.error("{bindPort:" + serverPort + "}", ex);
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void close() {
		bossEventLoop.shutdownGracefully();
		workerEventLoop.shutdownGracefully();
	}
}
