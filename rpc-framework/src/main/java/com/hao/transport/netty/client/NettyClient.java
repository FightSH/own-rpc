package com.hao.transport.netty.client;


import com.hao.common.factory.SingletonFactory;
import com.hao.transport.channelprovider.NettyChannelProvider;
import com.hao.transport.channelprovider.UnprocessedRequest;
import com.hao.transport.netty.coder.RPCMessageDecoder;
import com.hao.transport.netty.coder.RPCMessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class NettyClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;

    public NettyClient() {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new IdleStateHandler(0, 6, 0, TimeUnit.SECONDS));
                        ch.pipeline().addLast(new RPCMessageEncoder());
                        ch.pipeline().addLast(new RPCMessageDecoder());
                        ch.pipeline().addLast(new NettyClientHandler());

                    }
                });

        logger.info("NettyClient is ready to connect...");
    }


    public Channel doConnect(InetSocketAddress inetSocketAddress) {
        CompletableFuture<Channel> completeFuture = new CompletableFuture<Channel>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                logger.info("The client has connected [{}] successful!", inetSocketAddress.toString());
                completeFuture.complete(future.channel());
            } else {
                logger.error("connect is failed");
                throw new IllegalStateException();
            }
        });

        try {
            return completeFuture.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void close() {
        eventLoopGroup.shutdownGracefully();
    }


}
