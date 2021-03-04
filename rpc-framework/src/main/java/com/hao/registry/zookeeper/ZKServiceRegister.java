package com.hao.registry.zookeeper;

import com.hao.registry.ServiceRegister;
import com.hao.registry.zookeeper.util.CuratorUtils;

import java.net.InetSocketAddress;

public class ZKServiceRegister implements ServiceRegister {

    @Override
    public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        final String servicePath = CuratorUtils.ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName + inetSocketAddress.toString();
        CuratorUtils.createPersistentNode(CuratorUtils.getZkClient(), servicePath);


    }
}
