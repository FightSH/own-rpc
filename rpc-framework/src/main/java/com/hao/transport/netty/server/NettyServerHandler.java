package com.hao.transport.netty.server;

import com.hao.common.constant.RPCConstants;
import com.hao.common.factory.SingletonFactory;
import com.hao.transport.dto.RPCMessage;
import com.hao.transport.dto.RPCRequest;
import com.hao.transport.dto.RPCResponse;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    private final RequestHandler requestHandler;

    public NettyServerHandler() {
        this.requestHandler = SingletonFactory.getInstance(RequestHandler.class);
    }

    //todo netty的入站出站方法需要再复习下
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            RPCResponse<Object> response = new RPCResponse<>();
            if (msg instanceof RPCMessage) {

                RPCMessage temp = (RPCMessage) msg;
                final RPCMessage rpcMessage = new RPCMessage();
                if (temp.getMessageType() == RPCConstants.REQUEST_TYPE) {
                    final RPCRequest data = (RPCRequest) temp.getData();
                    final Object result = requestHandler.handle(data);



                    if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                        response = RPCResponse.success(result, data.getRequestId());

                    } else {
                        response = RPCResponse.failed();

                    }
                    rpcMessage.setData(response);
                    rpcMessage.setCompress(RPCConstants.CompressTypeEnum.GZIP.getCode());
                    rpcMessage.setCodec(RPCConstants.SerializationTypeEnum.KYRO.getCode());
                    rpcMessage.setMessageType(RPCConstants.RESPONSE_TYPE);

                } else if (temp.getMessageType() == RPCConstants.HEARTBEAT_REQUEST_TYPE) {
                    rpcMessage.setMessageType(RPCConstants.HEARTBEAT_RESPONSE_TYPE);
                    rpcMessage.setCodec(RPCConstants.SerializationTypeEnum.KYRO.getCode());
                    rpcMessage.setCompress(RPCConstants.CompressTypeEnum.GZIP.getCode());
                    rpcMessage.setData(RPCConstants.TOK);

                }
                ctx.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }


        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                logger.info("idle check happen, so close the connection");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("server catch exception", cause);
        ctx.close();
    }

}
