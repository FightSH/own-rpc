package com.hao.transport.proxy;

import com.hao.transport.TransportInterface;
import com.hao.transport.dto.RPCRequest;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

public class RPCProxy implements InvocationHandler, MethodInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RPCProxy.class);

    private TransportInterface transport;

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
        logger.info("增强方法: [{}]", method.getName());;
        //封装RPCRequest类
        final RPCRequest request = new RPCRequest();
        request.setMethodName(method.getName());
        request.setInterfaceName(method.getDeclaringClass().getName());
        request.setParameters(args);
        request.setRequestId(UUID.randomUUID().toString());

        transport.sendRequest(request);


        return null;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        return null;
    }
}
