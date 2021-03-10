package com.hao.registry;

import com.hao.spi.RPCSPI;

import java.net.InetSocketAddress;

@RPCSPI
public interface ServiceDiscovery {

    /**
     * 服务发现
     *
     * @param rpcServiceName
     * @return
     */
    InetSocketAddress discoveryService(String rpcServiceName);

}
