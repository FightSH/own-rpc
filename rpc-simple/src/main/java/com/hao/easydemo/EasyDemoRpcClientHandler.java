package com.hao.easydemo;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.CompletableFuture;

public class EasyDemoRpcClientHandler extends ChannelInboundHandlerAdapter {

    private ChannelHandlerContext context;
    private String result;
    private String para;


    public void setPara(String para) {
        this.para = para;
    }


    public CompletableFuture<String> sendRequest() {
        CompletableFuture<String> result = new CompletableFuture<>();

        context.writeAndFlush(this.para).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                System.out.println("client send message: "+ this.para);
            } else {
                future.channel().close();
                result.completeExceptionally(future.cause());
                System.out.println("Send failed: "+ this.para);
            }
        });

        return result;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel active");
        context = ctx;
    }

    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("client read");
        result = msg.toString();
        System.out.println(result);
    }

}
