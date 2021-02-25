package com.hao.transport.netty.client;


import com.hao.common.factory.SingletonFactory;
import com.hao.transport.channelprovider.NettyChannelProvider;
import com.hao.transport.channelprovider.UnprocessedRequest;
import com.hao.transport.dto.RPCRequest;
import com.hao.transport.dto.RPCResponse;
import com.hao.transport.netty.coder.NettyKryoDecoder;
import com.hao.transport.netty.coder.NettyKryoEncoder;
import com.hao.transport.serializer.KryoSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.CompleteFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class NettyClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private  final Bootstrap bootstrap;
    private final UnprocessedRequest unprocessedRequests;
    private final NettyChannelProvider channelProvider;

    public NettyClient() {
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        final KryoSerializer serializer = new KryoSerializer();
        bootstrap.group(nioEventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new NettyKryoDecoder(serializer, RPCResponse.class));
                        ch.pipeline().addLast(new NettyKryoEncoder(serializer, RPCRequest.class));
                        ch.pipeline().addLast(new NettyClientHandler());

                    }
                });


        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequest.class);
        this.channelProvider = SingletonFactory.getInstance(NettyChannelProvider.class);
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


}
