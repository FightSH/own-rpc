package com.hao.spring;

import com.hao.annotation.RPCReference;
import com.hao.annotation.RPCService;
import com.hao.common.factory.SingletonFactory;
import com.hao.registry.RpcServiceProperties;
import com.hao.spi.ExtensionLoader;
import com.hao.transport.TransportInterface;
import com.hao.transport.proxy.RPCProxy;
import com.hao.transport.serviceprovider.ServiceProvider;
import com.hao.transport.serviceprovider.ServiceProviderImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

/**
 * 结合spring，在bean生成时注册服务以及生成代理
 */
public class RPCBeanPostProcessor implements BeanPostProcessor {


    private final ServiceProvider serviceProvider;
    private final TransportInterface rpcClient;


    public RPCBeanPostProcessor() {
        this.rpcClient = ExtensionLoader.getExtensionLoader(TransportInterface.class).getExtension("netty");
        this.serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);
    }


    /**
     * 注册服务
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(RPCService.class)) {
            RPCService rpcService = bean.getClass().getAnnotation(RPCService.class);

            RpcServiceProperties rpcServiceProperties = new RpcServiceProperties();
            rpcServiceProperties.setGroup(rpcService.group());
            rpcServiceProperties.setVersion(rpcService.version());
            serviceProvider.publishService(bean, rpcServiceProperties);
        }

        return bean;
    }

    /**
     * 生成代理对象
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        final Class<?> beanClass = bean.getClass();
        final Field[] declaredFields = beanClass.getDeclaredFields();

        for (Field declaredField : declaredFields) {
            if (declaredField.isAnnotationPresent(RPCReference.class)) {
                final RPCProxy rpcProxy = new RPCProxy(rpcClient);
                //可通过注解选择
                final Object proxyByJDK = rpcProxy.getProxyByJDK(declaredField.getType());
                declaredField.setAccessible(true);
                try {
                    declaredField.set(bean, proxyByJDK);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return bean;
    }
}
