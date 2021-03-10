package com.hao.transport.serviceprovider;

import com.hao.registry.RpcServiceProperties;
import com.hao.spi.RPCSPI;

@RPCSPI
public interface ServiceProvider {

    /**
     *  @param service
     * @param serviceClass
     * @param serviceProperties
     */
    void addService(Object service, Class<?> serviceClass, RpcServiceProperties serviceProperties);

    /**
     *
     * @param serviceProperties
     * @return
     */
    Object getService(RpcServiceProperties serviceProperties);

    /**
     *  @param service
     * @param serviceProperties
     */
    void publishService(Object service, RpcServiceProperties serviceProperties);


}
