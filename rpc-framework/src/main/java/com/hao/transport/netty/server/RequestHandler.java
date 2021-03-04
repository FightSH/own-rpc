package com.hao.transport.netty.server;

import com.hao.common.constant.RPCErrorMessageEnum;
import com.hao.common.exception.RPCException;
import com.hao.common.factory.SingletonFactory;
import com.hao.transport.dto.RPCRequest;
import com.hao.transport.serviceprovider.ServiceProvider;
import com.hao.transport.serviceprovider.ServiceProviderImpl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 真正处理请求的逻辑
 */
public class RequestHandler {

    private final ServiceProvider serviceProvider;

    public RequestHandler() {
        serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);
    }


    public Object handle(RPCRequest rpcRequest) {
        Object result = null;
        try {
            final Object serviceBean = serviceProvider.getService(rpcRequest.getInterfaceName());
            final String methodName = rpcRequest.getMethodName();
            final Object parameters = rpcRequest.getParameters();

            Class<?> serviceClass = serviceBean.getClass();
            Class<?>[] parameterTypes = rpcRequest.getParameterTypes();
            // 通过反射调用客户端请求的方法
            Method method = serviceClass.getMethod(methodName, parameterTypes);

            method.setAccessible(true);
            result = method.invoke(serviceBean, parameters);
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
            throw new RPCException(e.getMessage(), e);
        }
        return result;
    }
}



