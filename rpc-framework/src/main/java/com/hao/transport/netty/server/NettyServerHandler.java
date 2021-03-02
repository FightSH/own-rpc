package com.hao.transport.netty.server;

import com.hao.common.constant.RPCConstants;
import com.hao.common.factory.SingletonFactory;
import com.hao.transport.dto.RPCMessage;
import com.hao.transport.dto.RPCRequest;
import com.hao.transport.dto.RPCResponse;
import io.netty.channel.*;
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
                if (temp.getMessageType() == RPCConstants.REQUEST_TYPE) {
                    final RPCRequest data = (RPCRequest) temp.getData();
                    final Object result = requestHandler.handle(data);

                    if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                        response = RPCResponse.success(result, data.getRequestId());
                        final RPCMessage rpcMessage = new RPCMessage();
                        rpcMessage.setData(response);
                        rpcMessage.setCompress(RPCConstants.CompressTypeEnum.GZIP.getCode());
                        rpcMessage.setCodec(RPCConstants.SerializationTypeEnum.KYRO.getCode());
                        rpcMessage.setMessageType(RPCConstants.RESPONSE_TYPE);

                        ctx.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);

                    } else {
                        response = RPCResponse.failed();
                        final RPCMessage rpcMessage = new RPCMessage();
                        rpcMessage.setData(response);
                        rpcMessage.setCompress(RPCConstants.CompressTypeEnum.GZIP.getCode());
                        rpcMessage.setCodec(RPCConstants.SerializationTypeEnum.KYRO.getCode());
                        rpcMessage.setMessageType(RPCConstants.RESPONSE_TYPE);

                        ctx.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                    }

                }
            }


        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("server catch exception", cause);
        ctx.close();
    }

}
