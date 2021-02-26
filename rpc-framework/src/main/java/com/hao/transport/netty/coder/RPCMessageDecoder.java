package com.hao.transport.netty.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 使用LengthFieldBasedFrameDecoder解决拆包问题
 *
 *  custom protocol decoder
 *  <pre>
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
 *
 *  有个LengthFieldBasedFrameDecoder知识可见 https://www.jianshu.com/p/a0a51fd79f62 和 https://zhuanlan.zhihu.com/p/95621344
 *
 */
public class RPCMessageDecoder extends LengthFieldBasedFrameDecoder {


    public RPCMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength,
                             int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }


    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        return super.decode(ctx, in);
    }
}
