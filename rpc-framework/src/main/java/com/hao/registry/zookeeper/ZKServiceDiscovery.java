package com.hao.registry.zookeeper;

import com.hao.common.constant.RPCErrorMessageEnum;
import com.hao.common.exception.RPCException;
import com.hao.registry.ServiceDiscovery;
import com.hao.registry.loadbalance.LoadBalance;
import com.hao.registry.zookeeper.util.CuratorUtils;
import com.hao.spi.ExtensionLoader;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

public class ZKServiceDiscovery implements ServiceDiscovery {

    private static final Logger logger = LoggerFactory.getLogger(ZKServiceDiscovery.class);

    private LoadBalance loadBalance;

    public ZKServiceDiscovery() {
        final LoadBalance loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension("loadBalance");
    }


    @Override
    public InetSocketAddress discoveryService(String rpcServiceName) {
        final CuratorFramework zkClient = CuratorUtils.getZkClient();
        final List<String> childrenNodes = CuratorUtils.getChildrenNodes(zkClient, rpcServiceName);
        if (childrenNodes == null || childrenNodes.size() == 0) {
            throw new RPCException(RPCErrorMessageEnum.NOT_FOUND_NEED_SERVICE, rpcServiceName);
        }
        final String target = loadBalance.selectServiceAddress(childrenNodes, rpcServiceName);

        final String[] split = target.split(":");
        String host = split[0];
        int port = Integer.parseInt(split[1]);

        return new InetSocketAddress(host, port);

    }
}
