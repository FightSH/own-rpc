package com.hao.transport.netty.coder.kryo;

import com.hao.transport.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class NettyKryoDecoder extends ByteToMessageDecoder {

    private static final Logger logger = LoggerFactory.getLogger(NettyKryoDecoder.class);

    private final Serializer serializer;

    private final Class<?> aClass;

    private static final int BODY_LENGTH = 4;

    public NettyKryoDecoder(Serializer serializer, Class<?> aClass) {
        super();
        this.serializer = serializer;
        this.aClass = aClass;
    }


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() >= BODY_LENGTH) {

            in.markReaderIndex();
            //信息长度，出站时所写入
            final int length = in.readInt();


            if (in.readableBytes() < length) {
                in.resetReaderIndex();
                return;
            }

            final byte[] body = new byte[length];
            in.readBytes(body);
            final Object object = serializer.deserialize(body, aClass);
            out.add(object);
            logger.info("decode success");

        }


    }
}
