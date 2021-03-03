package com.hao.registry;

import java.net.InetSocketAddress;

public interface ServiceDiscovery {

    /**
     * 服务发现
     * @param rpcServiceName
     * @return
     */
    InetSocketAddress discoveryService(String rpcServiceName);

}
