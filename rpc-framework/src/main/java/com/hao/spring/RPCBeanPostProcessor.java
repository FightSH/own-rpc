package com.hao.spring;

import com.hao.annotation.RPCReference;
import com.hao.annotation.RPCService;
import com.hao.common.config.RPCServiceConfig;
import com.hao.common.factory.SingletonFactory;
import com.hao.registry.RpcServiceProperties;
import com.hao.spi.ExtensionLoader;
import com.hao.transport.TransportInterface;
import com.hao.transport.proxy.RPCProxy;
import com.hao.transport.serviceprovider.ServiceProvider;
import com.hao.transport.serviceprovider.ServiceProviderImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * 结合spring，在bean生成时注册服务以及生成代理
 */
@Component
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
            RPCReference rpcReference = declaredField.getAnnotation(RPCReference.class);
            if (rpcReference != null) {

                RPCServiceConfig serviceConfig = new RPCServiceConfig(rpcReference.version(), rpcReference.group());
                String proxyType = rpcReference.proxyType();

                final RPCProxy rpcProxy = new RPCProxy(rpcClient,serviceConfig);
                //可通过注解选择
                Object proxy = null;
                if (proxyType.equals("jdk")) {
                     proxy = rpcProxy.getProxyByJDK(declaredField.getType());
                }else {
                     proxy = rpcProxy.getProxyByCGLIB(declaredField.getType());
                }

                declaredField.setAccessible(true);
                try {
                    declaredField.set(bean, proxy);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return bean;
    }
}
