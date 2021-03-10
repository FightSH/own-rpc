package com.hao.registry.loadbalance;

import com.hao.spi.RPCSPI;

import java.util.List;
@RPCSPI
public interface LoadBalance {

    String selectServiceAddress(List<String> serviceAddresses, String rpcServiceName);

}
