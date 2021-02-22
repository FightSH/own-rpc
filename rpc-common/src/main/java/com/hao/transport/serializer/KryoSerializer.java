package com.hao.transport.serializer;


import com.esotericsoftware.kryo.Kryo;
import com.hao.transport.dto.RPCRequest;
import com.hao.transport.dto.RPCResponse;

/**
 * kryo序列化方式
 */
public class KryoSerializer implements Serializer {


    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RPCRequest.class);
        kryo.register(RPCResponse.class);
        //设置是否关闭注册行为
        kryo.setReferences(true);
        //设置是否关闭循环引用
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    public byte[] serialize(Object object) {
        return new byte[0];
    }

    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        return null;
    }
}
