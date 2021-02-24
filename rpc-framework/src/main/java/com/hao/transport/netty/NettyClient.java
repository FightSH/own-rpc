package com.hao.transport.netty;


import com.hao.transport.dto.RPCRequest;
import com.hao.transport.dto.RPCResponse;
import com.hao.transport.netty.coder.NettyKryoDecoder;
import com.hao.transport.netty.coder.NettyKryoEncoder;
import com.hao.transport.serializer.KryoSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;

public class NettyClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private static final Bootstrap bootstrap;

    public NettyClient() {

    }

    static {
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

        logger.info("NettyClient is ready to connect...");
    }


    public Channel doConnect(InetSocketAddress inetSocketAddress) {
        return null;

    }


}
