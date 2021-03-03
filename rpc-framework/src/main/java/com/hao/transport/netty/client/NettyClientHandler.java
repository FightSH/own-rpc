package com.hao.transport.netty.client;

import com.hao.common.constant.RPCConstants;
import com.hao.common.factory.SingletonFactory;
import com.hao.transport.channelprovider.NettyChannelProvider;
import com.hao.transport.channelprovider.UnprocessedRequest;
import com.hao.transport.dto.RPCMessage;
import com.hao.transport.dto.RPCResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
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
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            logger.info("client receive msg: [{}]", msg.toString());
            if (msg instanceof RPCMessage) {
                RPCMessage temp = (RPCMessage) msg;

                if (temp.getMessageType() == RPCConstants.RESPONSE_TYPE) {

                    final RPCResponse<Object> rpcResponse = (RPCResponse<Object>) temp.getData();
                    unprocessedRequest.complete(rpcResponse);

                } else if (temp.getMessageType() == RPCConstants.HEARTBEAT_RESPONSE_TYPE) {
                    RPCResponse<Object> rpcResponse = (RPCResponse<Object>) temp.getData();
                    unprocessedRequest.complete(rpcResponse);
                }

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

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if (evt instanceof IdleStateEvent) {
            final IdleState state = ((IdleStateEvent) evt).state();
            if (IdleState.WRITER_IDLE == state) {
                final RPCMessage rpcMessage = new RPCMessage();
                rpcMessage.setMessageType(RPCConstants.HEARTBEAT_REQUEST_TYPE);
                rpcMessage.setCodec(RPCConstants.SerializationTypeEnum.KYRO.getCode());
                rpcMessage.setCompress(RPCConstants.CompressTypeEnum.GZIP.getCode());
                rpcMessage.setData(RPCConstants.TIK);

                ctx.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);


            }

        } else {
            super.userEventTriggered(ctx, evt);
        }


    }
}
