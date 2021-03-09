package com.hao.transport.netty.coder;

import com.hao.common.compress.Compress;
import com.hao.common.constant.RPCConstants;
import com.hao.spi.ExtensionLoader;
import com.hao.transport.dto.RPCMessage;
import com.hao.transport.dto.RPCRequest;
import com.hao.transport.dto.RPCResponse;
import com.hao.transport.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * 使用LengthFieldBasedFrameDecoder解决拆包问题
 * <p>
 * custom protocol decoder
 * <pre>
 *    0     1     2     3     4        5     6     7     8         9          10      11     12  13  14   15 16
 *    +-----+-----+-----+-----+--------+----+----+----+------+-----------+-------+----- --+-----+-----+-------+
 *    |   magic   code        |version | full length         | messageType| codec|compress|    RequestId       |
 *    +-----------------------+--------+---------------------+-----------+-----------+-----------+------------+
 *    |                                                                                                       |
 *    |                                         body                                                          |
 *    |                                                                                                       |
 *    |                                        ... ...                                                        |
 *    +-------------------------------------------------------------------------------------------------------+
 *  4B  magic code（魔法数）   1B version（版本）   4B full length（消息长度）    1B messageType（消息类型）
 *  1B compress（压缩类型） 1B codec（序列化类型）    4B  requestId（请求的Id）
 *  body（object类型数据）
 *  </pre>
 * <p>
 * 有关LengthFieldBasedFrameDecoder知识可见 https://www.jianshu.com/p/a0a51fd79f62 和 https://zhuanlan.zhihu.com/p/95621344
 */
public class RPCMessageDecoder extends LengthFieldBasedFrameDecoder {

    private static final Logger logger = LoggerFactory.getLogger(RPCMessageDecoder.class);

    //todo 去除requestId，需要对下列值进行调整
    public RPCMessageDecoder() {
        // lengthFieldOffset: magic code is 4B, and version is 1B, and then full length. so value is 5
        // lengthFieldLength: full length is 4B. so value is 4
        // lengthAdjustment: full length include all data and read 9 bytes before, so the left length is (fullLength-9). so values is -9
        // initialBytesToStrip: we will check magic code and version manually, so do not strip any bytes. so values is 0
        this(RPCConstants.MAX_FRAME_LENGTH, 5, 4, -9, 0);
    }


    public RPCMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength,
                             int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }


    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decoded = super.decode(ctx, in);
        if (decoded instanceof ByteBuf) {
            ByteBuf frame = (ByteBuf) decoded;
            if (frame.readableBytes() >= RPCConstants.TOTAL_LENGTH) {
                try {
                    return decodeFrame(frame);
                } catch (Exception e) {
                    logger.error("Decode frame error!", e);
                    throw e;
                } finally {
                    frame.release();
                }
            }

        }
        return decoded;


    }

    private Object decodeFrame(ByteBuf in) {
        int len = RPCConstants.MAGIC_NUMBER.length;
        final byte[] tmp = new byte[len];
        in.readBytes(tmp);
        for (int i = 0; i < len; i++) {
            if (tmp[i] != RPCConstants.MAGIC_NUMBER[i]) {
                throw new IllegalArgumentException("Unknown magic code: " + Arrays.toString(tmp));
            }
        }

        byte version = in.readByte();
        if (version != RPCConstants.RPC_VERSION) {
            throw new RuntimeException("version isn't compatible" + version);
        }

        int fullLength = in.readInt();
        // build RpcMessage object
        byte messageType = in.readByte();
        byte codecType = in.readByte();
        byte compressType = in.readByte();

        final RPCMessage message = new RPCMessage();

        message.setMessageType(messageType);
        message.setCodec(codecType);
        message.setCompress(compressType);

        int bodyLength = fullLength - RPCConstants.HEAD_LENGTH;

        if (bodyLength > 0) {

            byte[] bodyObject = new byte[bodyLength];
            in.readBytes(bodyObject);

            final Compress compress = ExtensionLoader.getExtensionLoader(Compress.class).getExtension("gzip");
            bodyObject = compress.decompress(bodyObject);


            final Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension("kryo");


            if (messageType == RPCConstants.REQUEST_TYPE) {
                RPCRequest tmpValue = serializer.deserialize(bodyObject, RPCRequest.class);
                message.setData(tmpValue);
            }else {
                RPCResponse tmpValue = serializer.deserialize(bodyObject, RPCResponse.class);
                message.setData(tmpValue);
            }

        }

        return message;
    }

}
