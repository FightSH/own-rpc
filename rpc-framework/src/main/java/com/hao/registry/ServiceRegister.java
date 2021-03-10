package com.hao.registry;

import com.hao.spi.RPCSPI;

import java.net.InetSocketAddress;

@RPCSPI
public interface ServiceRegister {

    /**
     * 服务注册
     *
     * @param rpcServiceName
     * @param inetSocketAddress
     */
    void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress);

}
