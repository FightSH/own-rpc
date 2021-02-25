package com.hao.transport.netty.client;

import com.hao.common.factory.SingletonFactory;
import com.hao.transport.channelprovider.NettyChannelProvider;
import com.hao.transport.channelprovider.UnprocessedRequest;
import com.hao.transport.dto.RPCResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);


    private final UnprocessedRequest unprocessedRequest;
    private final NettyClient nettyClient;

    public NettyClientHandler() {
        this.unprocessedRequest = SingletonFactory.getInstance(UnprocessedRequest.class);
        this.nettyClient = SingletonFactory.getInstance(NettyClient.class);

    }

    /**
     * 读取server返回的信息
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            logger.info("client receive msg: [{}]", msg.toString());
            if (msg instanceof RPCResponse) {
                RPCResponse<Object> rpcResponse = (RPCResponse) msg;
                unprocessedRequest.complete(rpcResponse);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("client caught exception", cause);
        ctx.close();
    }

    //todo 覆写心跳连接方法
}
