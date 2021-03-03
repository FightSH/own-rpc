package com.hao.registry.loadbalance;

import java.util.List;

public interface LoadBalance {

    String selectServiceAddress(List<String> serviceAddresses, String rpcServiceName);

}
