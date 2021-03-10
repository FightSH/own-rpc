package com.hao.transport.serviceprovider;

import com.hao.common.constant.RPCErrorMessageEnum;
import com.hao.common.exception.RPCException;
import com.hao.registry.RpcServiceProperties;
import com.hao.registry.ServiceRegister;
import com.hao.spi.ExtensionLoader;
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

        serviceRegister = ExtensionLoader.getExtensionLoader(ServiceRegister.class).getExtension("zookeeper");
    }


    @Override
    public void addService(Object service, Class<?> serviceClass, RpcServiceProperties serviceProperties) {
        String rpcServiceInfo = serviceProperties.toRpcServiceInfo();
        if (registeredService.contains(rpcServiceInfo)) {
            return;
        }
        registeredService.add(rpcServiceInfo);
        serviceMap.put(rpcServiceInfo, service);

    }

    @Override
    public Object getService(RpcServiceProperties serviceProperties) {
        Object service = serviceMap.get(serviceProperties.toRpcServiceInfo());
        if (null == service) {
            throw new RPCException(RPCErrorMessageEnum.NOT_FOUND_NEED_SERVICE);
        }
        return service;
    }


    @Override
    public void publishService(Object service, RpcServiceProperties serviceProperties) {
        try {
            String host = InetAddress.getLocalHost().getHostAddress();
            Class<?> serviceRelatedInterface = service.getClass().getInterfaces()[0];
            String serviceName = serviceRelatedInterface.getCanonicalName();
            serviceProperties.setServiceName(serviceName);
            this.addService(service, serviceRelatedInterface, serviceProperties);
            serviceRegister.registerService(serviceProperties.toRpcServiceInfo(), new InetSocketAddress(host, NettyServer.PORT));
        } catch (UnknownHostException e) {
            logger.error("occur exception when getHostAddress", e);
        }

    }

}
