package com.hao.transport.netty.server;

import com.hao.transport.dto.RPCRequest;
import com.hao.transport.dto.RPCResponse;
import com.hao.transport.netty.client.NettyClient;
import com.hao.transport.netty.coder.NettyKryoDecoder;
import com.hao.transport.netty.coder.NettyKryoEncoder;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyServer {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private  final int port = 9999;

    public NettyServer(int port) {
        this.port = port;
    }

    private void run() {
        final NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        final NioEventLoopGroup workGroup = new NioEventLoopGroup();

        final KryoSerializer serializer = new KryoSerializer();

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
                        ch.pipeline().addLast(new NettyKryoDecoder(serializer, RPCRequest.class));
                        ch.pipeline().addLast(new NettyKryoEncoder(serializer, RPCResponse.class));
                        ch.pipeline().addLast(new NettyServerHandler());
                    }
                });

        try {
            final ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
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
