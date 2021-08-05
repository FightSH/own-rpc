package com.hao.registry.zookeeper.util;

import com.hao.common.config.RPCConfigEnum;
import com.hao.common.utils.PropertiesFileUtil;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CuratorUtils {

    private static final Logger logger = LoggerFactory.getLogger(CuratorUtils.class);

    private static final int BASE_SLEEP_TIME = 1000;
    private static final int MAX_RETRIES = 3;
    public static final String ZK_REGISTER_ROOT_PATH = "/own-rpc";
    private static final Map<String, List<String>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap();
    private static final Set<String> REGISTERED_PATH_SET = ConcurrentHashMap.newKeySet();
    private static CuratorFramework zkClient;
    private static String DEFAULT_ZOOKEEPER_ADDRESS_PORT = "47.100.79.39:8888";

    private CuratorUtils() {

    }

    /**
     * 获取或初始化zkClient
     *
     * @return
     */
    public static CuratorFramework getZkClient() {


        Properties properties = PropertiesFileUtil.readPropertiesFile(RPCConfigEnum.RPC_CONFIG_PATH.getPropertyValue());
        if (properties != null) {
            DEFAULT_ZOOKEEPER_ADDRESS_PORT = properties.getProperty(RPCConfigEnum.ZK_ADDRESS.getPropertyValue());
        }

        if (zkClient != null && zkClient.getState() == CuratorFrameworkState.STARTED) {
            return zkClient;
        }
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
        zkClient = CuratorFrameworkFactory.builder()
                .connectString(DEFAULT_ZOOKEEPER_ADDRESS_PORT)
                .retryPolicy(retryPolicy)
                .build();
        zkClient.start();
        return zkClient;
    }


    /**
     * 创建持久节点
     *
     * @param zkClient
     * @param path
     */
    public static void createPersistentNode(CuratorFramework zkClient, String path) {

        try {
            if (REGISTERED_PATH_SET.contains(path) || zkClient.checkExists().forPath(path) != null) {
                logger.info("the node already exists,the node is [{}]", path);
            } else {
                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
                logger.info("the node was created successful. the node is :[{}]", path);
            }
            REGISTERED_PATH_SET.add(path);
        } catch (Exception e) {
            logger.error("create persistent node for path [{}] fail", path);
        }

    }

    public static List<String> getChildrenNodes(CuratorFramework zkClient, String nodeName) {
        if (SERVICE_ADDRESS_MAP.containsKey(nodeName)) {
            return SERVICE_ADDRESS_MAP.get(nodeName);
        }

        List<String> result = null;
        String path = ZK_REGISTER_ROOT_PATH + "/" + nodeName;

        try {
            result = zkClient.getChildren().forPath(path);
            SERVICE_ADDRESS_MAP.put(nodeName, result);
            registerWatcher(nodeName, zkClient);
        } catch (Exception e) {
            logger.error("get children nodes for path [{}] failed",nodeName);
        }
        return result;

    }

    private static void registerWatcher(String nodeName, CuratorFramework zkClient) throws Exception {
        String servicePath = ZK_REGISTER_ROOT_PATH + "/" + nodeName;
        final PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, servicePath, true);
        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                List<String> serviceAddresses = curatorFramework.getChildren().forPath(servicePath);
                SERVICE_ADDRESS_MAP.put(nodeName, serviceAddresses);
            }
        });

        pathChildrenCache.start();
    }

    public static void clearRegistry(CuratorFramework zkClient) {
        REGISTERED_PATH_SET.stream().parallel().forEach(p->{
            try {
                zkClient.delete().forPath(p);
            } catch (Exception e) {
                logger.error("clear registry for path [{}] fail", p);
            }
        });
        logger.info("All registered services on the server are cleared:[{}]", REGISTERED_PATH_SET.toString());
    }


}
