package com.hao.transport.netty.server;

import com.hao.transport.dto.RPCRequest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 真正处理请求的逻辑
 */
public class RequestHandler {


    private static ConcurrentHashMap<String, Object> handlerMap = new ConcurrentHashMap<>();

    public Object handle(RPCRequest rpcRequest) {

        final String interfaceName = rpcRequest.getInterfaceName();
        final String methodName = rpcRequest.getMethodName();
        final Object parameters = rpcRequest.getParameters();


        if (handlerMap.containsKey(interfaceName)) {
            try {
                final Object serviceBean = handlerMap.get(interfaceName);
                Class<?> serviceClass = serviceBean.getClass();
                Class<?>[] parameterTypes = rpcRequest.getParameterTypes();
                // 通过反射调用客户端请求的方法
                Method method = serviceClass.getMethod(methodName, parameterTypes);
                method.setAccessible(true);
                return method.invoke(serviceBean, parameters);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

        }

        return null;
    }
}
