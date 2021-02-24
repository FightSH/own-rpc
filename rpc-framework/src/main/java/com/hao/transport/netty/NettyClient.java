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

import java.util.concurrent.atomic.AtomicReference;

public class NettyClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private final String host;

    private final int port;

    private static final Bootstrap bootstrap;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
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

        logger.info("NettyClient is ok ...");
    }


    public RPCResponse sendRequest(RPCRequest rpcRequest) {
        try {

            final ChannelFuture future = bootstrap.connect(host, port).sync();
            logger.info("client channel is connecting {}", host + ":" + port);

            final Channel channel = future.channel();

            channel.writeAndFlush(rpcRequest).addListener(future1 -> {
                if (future1.isSuccess()) {
                    logger.info("send success...");
                } else {
                    logger.error("send failed...");
                }
            });

            channel.closeFuture().sync();
            AttributeKey<RPCResponse> key = AttributeKey.valueOf("rpcResponse");
            return channel.attr(key).get();


        } catch (InterruptedException e) {
            logger.error("something is wrong...");
        }

        return null;
    }

    public static void main(String[] args) {
        final RPCRequest rpcRequest = new RPCRequest("interfaceName", "methodName");
        logger.info("开始了");
        final NettyClient nettyClient = new NettyClient("127.0.0.1", 9999);

        final RPCResponse rpcResponse = nettyClient.sendRequest(rpcRequest);

        logger.info(rpcResponse.toString());

    }


}
