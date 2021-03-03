package com.hao.registry;

import java.net.InetSocketAddress;

public interface ServiceRegister {

    /**
     * 服务注册
     * @param rpcServiceName
     * @param inetSocketAddress
     */
    void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress);

}
