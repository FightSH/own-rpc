package com.hao.transport.netty.server;

import com.hao.common.factory.SingletonFactory;
import com.hao.common.utils.ThreadPoolFactoryUtils;
import com.hao.registry.RpcServiceProperties;
import com.hao.shutdown.CustomShutdownHook;
import com.hao.transport.netty.client.NettyClient;
import com.hao.transport.netty.coder.RPCMessageDecoder;
import com.hao.transport.netty.coder.RPCMessageEncoder;
import com.hao.transport.serviceprovider.ServiceProvider;
import com.hao.transport.serviceprovider.ServiceProviderImpl;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class NettyServer {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    public static final int PORT = 9999;

    private final ServiceProvider serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);

    public void registerService(Object service, RpcServiceProperties rpcServiceProperties) {
        serviceProvider.publishService(service, rpcServiceProperties);
    }


    public void start() throws UnknownHostException {
        CustomShutdownHook.getInstance().clear();
        String host = InetAddress.getLocalHost().getHostAddress();

        DefaultEventExecutorGroup serviceHandlerGroup = new DefaultEventExecutorGroup(
                Runtime.getRuntime().availableProcessors() * 2,
                ThreadPoolFactoryUtils.createThreadFactory("service-handler-group", false));

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            final ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
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
                            ch.pipeline().addLast(serviceHandlerGroup, new NettyServerHandler());
                        }
                    });


            ChannelFuture f = serverBootstrap.bind(host, PORT).sync();
            // 等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("occur exception when start server:", e);
        } finally {
            logger.error("shutdown bossGroup and workerGroup");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            serviceHandlerGroup.shutdownGracefully();
        }

    }


}
