package com.hao.transport.serviceprovider;

import com.hao.common.constant.RPCErrorMessageEnum;
import com.hao.common.exception.RPCException;
import com.hao.registry.ServiceRegister;
import com.hao.registry.zookeeper.ZKServiceRegister;
import com.hao.transport.netty.server.NettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceProviderImpl implements ServiceProvider {

    private static final Logger logger = LoggerFactory.getLogger(ServiceProviderImpl.class);

    private final Map<String, Object> serviceMap;
    private final Set<String> registeredService;
    private final ServiceRegister serviceRegister;

    public ServiceProviderImpl() {
        serviceMap = new ConcurrentHashMap<>();
        registeredService = ConcurrentHashMap.newKeySet();
        serviceRegister = new ZKServiceRegister();
    }



    @Override
    public void addService(Object service, Class<?> serviceClass, String serviceName) {
        if (registeredService.contains(serviceName)) {
            return;
        }
        registeredService.add(serviceName);
        serviceMap.put(serviceName, service);


    }

    @Override
    public Object getService(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if (null == service) {
            throw new RPCException(RPCErrorMessageEnum.NOT_FOUND_NEED_SERVICE);
        }
        return service;
    }

    @Override
    public void publishService(Object service, String serviceName) {
        try {
            String host = InetAddress.getLocalHost().getHostAddress();
            Class<?> serviceRelatedInterface = service.getClass().getInterfaces()[0];
            if (serviceName == null || serviceName.length() == 0) {
                serviceName = serviceRelatedInterface.getCanonicalName();
            }
            this.addService(service, serviceRelatedInterface, serviceName);
            serviceRegister.registerService(serviceName,new InetSocketAddress(host, NettyServer.PORT));
        } catch (UnknownHostException e) {
            logger.error("occur exception when getHostAddress", e);
        }

    }

}
