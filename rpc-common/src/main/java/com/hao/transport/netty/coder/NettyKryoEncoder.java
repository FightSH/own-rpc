package com.hao.transport.netty.coder;

import com.hao.transport.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyKryoEncoder extends MessageToByteEncoder<Object> {

    private static final Logger logger = LoggerFactory.getLogger(NettyKryoEncoder.class);

    private final Serializer serializer;

    private final Class<?> aClass;




    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (aClass.isInstance(msg)) {
            final byte[] bytes = serializer.serialize(msg);
            final int length = bytes.length;
            out.writeInt(length);
            out.writeBytes(bytes);
            logger.info("encode success");
        }
    }


    public NettyKryoEncoder(Serializer serializer, Class<?> aClass) {
        super();
        this.serializer = serializer;
        this.aClass = aClass;
    }


}
