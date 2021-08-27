package com.hao.transport.proxy;

import com.hao.common.config.RPCServiceConfig;
import com.hao.transport.TransportInterface;
import com.hao.transport.dto.RPCRequest;
import com.hao.transport.dto.RPCResponse;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class RPCProxy implements InvocationHandler, MethodInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RPCProxy.class);

    private TransportInterface transport;

    private RPCServiceConfig rpcServiceInfo;

    public RPCProxy(TransportInterface transport, RPCServiceConfig serviceConfig) {
        this.transport = transport;
        this.rpcServiceInfo = serviceConfig;
    }

    /**
     * 通过jdk方式获取代理
     * @param clazz
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getProxyByJDK(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    /**
     * 通过cglib方式获取代理
     * @param clazz
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getProxyByCGLIB(Class<T> clazz) {
        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(clazz.getClassLoader());
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(this);
        return (T) enhancer.create();
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        logger.info("增强方法: [{}]", method.getName());
        //封装RPCRequest类
        final RPCRequest request = new RPCRequest();
        request.setMethodName(method.getName());
        request.setInterfaceName(method.getDeclaringClass().getName());
        request.setParameters(args);
        request.setParameterTypes(method.getParameterTypes());
        request.setRequestId(UUID.randomUUID().toString());
        request.setGroup(rpcServiceInfo.getGroup());
        request.setVersion(rpcServiceInfo.getVersion());

        final CompletableFuture<RPCResponse<Object>> future  = (CompletableFuture<RPCResponse<Object>>) transport.sendRequest(request);
        final RPCResponse<Object> response = future.get();

        return response;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        logger.info("增强方法: [{}]", method.getName());

        //封装RPCRequest类
        final RPCRequest request = new RPCRequest();
        request.setMethodName(method.getName());
        request.setInterfaceName(method.getDeclaringClass().getName());
        request.setParameters(objects);
        request.setParameterTypes(method.getParameterTypes());
        request.setRequestId(UUID.randomUUID().toString());
        request.setGroup(rpcServiceInfo.getGroup());
        request.setVersion(rpcServiceInfo.getVersion());

        final CompletableFuture<RPCResponse<Object>> future  = (CompletableFuture<RPCResponse<Object>>) transport.sendRequest(request);
        final RPCResponse<Object> response = future.get();

        return response;
    }
}
