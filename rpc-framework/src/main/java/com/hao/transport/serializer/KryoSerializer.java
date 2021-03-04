package com.hao.transport.serializer;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.hao.common.exception.KryoSerializerException;
import com.hao.transport.dto.RPCRequest;
import com.hao.transport.dto.RPCResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * kryo序列化方式。kryo非线程安全，因此使用ThreadLocal进行保存
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
        //jdk 9 特性
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             final Output output = new Output(byteArrayOutputStream)) {

            final Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output, object);
            return output.toBytes();

        } catch (Exception e) {
            throw new KryoSerializerException("kryo序列化失败");
        } finally {
            kryoThreadLocal.remove();
        }


    }

    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             final Input input = new Input(byteArrayInputStream)) {

            final Kryo kryo = kryoThreadLocal.get();
            final T object = kryo.readObject(input, clazz);
            return clazz.cast(object);

        } catch (Exception e) {
            throw new KryoSerializerException("kryo反序列化失败");
        } finally {
            kryoThreadLocal.remove();
        }

    }
}
