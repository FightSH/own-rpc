package com.hao.transport.netty.coder;

import com.hao.common.compress.Compress;
import com.hao.common.constant.RPCConstants;
import com.hao.spi.ExtensionLoader;
import com.hao.transport.dto.RPCMessage;
import com.hao.transport.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * 有个LengthFieldBasedFrameDecoder知识可见 https://www.jianshu.com/p/a0a51fd79f62 和 https://zhuanlan.zhihu.com/p/95621344
 */
public class RPCMessageEncoder extends MessageToByteEncoder<RPCMessage> {

    private static final Logger logger = LoggerFactory.getLogger(RPCMessageEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, RPCMessage msg, ByteBuf out) throws Exception {

        try {


            out.writeBytes(RPCConstants.MAGIC_NUMBER);

            out.writeByte(RPCConstants.RPC_VERSION);

            out.writerIndex(out.writerIndex() + 4);

            final byte messageType = msg.getMessageType();
            out.writeByte(messageType);

//        out.writeByte()

            out.writeByte(msg.getCodec());

            byte[] bodyObject = null;
            int fullLength = RPCConstants.HEAD_LENGTH;


            if (messageType != RPCConstants.HEARTBEAT_REQUEST_TYPE && messageType != RPCConstants.HEARTBEAT_RESPONSE_TYPE) {


                final Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension("kryo");

                bodyObject = serializer.serialize(msg.getData());

                final Compress compress = ExtensionLoader.getExtensionLoader(Compress.class).getExtension("gzip");

                bodyObject = compress.compress(bodyObject);

                fullLength += bodyObject.length;
            }
            if (bodyObject != null) {
                out.writeBytes(bodyObject);
            }
            int writeIndex = out.writerIndex();
            out.writerIndex(writeIndex - fullLength + RPCConstants.MAGIC_NUMBER.length + 1);
            out.writeInt(fullLength);
            out.writerIndex(writeIndex);
        } catch (Exception e) {
            logger.error("Encode request error!", e);
        }
    }
}
