package com.hao.registry.zookeeper;

import com.hao.registry.ServiceDiscovery;
import com.hao.registry.loadbalance.LoadBalance;
import com.hao.registry.loadbalance.impl.RandomLoadBalance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class ZKServiceDiscovery implements ServiceDiscovery {

    private static final Logger logger = LoggerFactory.getLogger(ZKServiceDiscovery.class);

    private  LoadBalance loadBalance;

    public ZKServiceDiscovery() {
        this.loadBalance = new RandomLoadBalance();
    }


    @Override
    public InetSocketAddress discoveryService(String rpcServiceName) {

        return null;

    }
}
