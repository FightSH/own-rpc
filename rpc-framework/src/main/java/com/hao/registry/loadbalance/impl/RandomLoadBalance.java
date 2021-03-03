package com.hao.registry.loadbalance.impl;

import com.hao.registry.loadbalance.AbstractLoadBalance;

import java.util.List;
import java.util.Random;

public class RandomLoadBalance extends AbstractLoadBalance {
    @Override
    protected String doSelect(List<String> serviceAddresses, String rpcServiceName) {
        return serviceAddresses.get(new Random().nextInt(serviceAddresses.size()));
    }
}
