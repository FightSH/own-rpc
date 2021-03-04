package com.hao.transport.netty.server;

import com.hao.transport.dto.RPCRequest;
import com.hao.transport.dto.RPCResponse;
import com.hao.transport.netty.client.NettyClient;
import com.hao.transport.netty.coder.RPCMessageDecoder;
import com.hao.transport.netty.coder.RPCMessageEncoder;
import com.hao.transport.netty.coder.kryo.NettyKryoDecoder;
import com.hao.transport.netty.coder.kryo.NettyKryoEncoder;
import com.hao.transport.serializer.KryoSerializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class NettyServer {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    public static final int PORT = 9999;


    private void run() {
        final NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        final NioEventLoopGroup workGroup = new NioEventLoopGroup();

        final ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_BACKLOG, 128)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                        ch.pipeline().addLast(new RPCMessageDecoder());
                        ch.pipeline().addLast(new RPCMessageEncoder());
                        ch.pipeline().addLast(new NettyServerHandler());
                    }
                });

        try {
            final ChannelFuture channelFuture = serverBootstrap.bind(PORT).sync();
            logger.info("NettyServer is ok...");
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("something wrong");
        } finally {
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }


    }


}
