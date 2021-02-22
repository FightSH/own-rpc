package com.hao.easydemo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.io.IOException;

/**
 * 服务端，netty的编解码器都使用默认的，具体处理请求的逻辑在EasyDemoRpcServerHandler中处理
 */
public class EasyDemoRpcServer {
    private static final String host = "127.0.0.1";

    private static final int port = 9999;

    public static void main(String[] args) throws IOException {


        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);

        NioEventLoopGroup workGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        try {
            serverBootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();

                            pipeline.addLast(new StringDecoder());
                            pipeline.addLast(new StringEncoder());
                            pipeline.addLast(new EasyDemoRpcServerHandler());


                        }
                    });


            ChannelFuture channelFuture = serverBootstrap.bind(host, port).sync();

            ChannelFuture sync = channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }


}
