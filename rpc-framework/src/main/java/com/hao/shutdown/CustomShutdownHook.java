package com.hao.shutdown;

import com.hao.common.utils.ThreadPoolFactoryUtils;
import com.hao.registry.zookeeper.util.CuratorUtils;

public class CustomShutdownHook {

    private static final CustomShutdownHook CUSTOM_SHUTDOWN_HOOK = new CustomShutdownHook();

    public static CustomShutdownHook getInstance() {
        return CUSTOM_SHUTDOWN_HOOK;
    }

    public void clear() {
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            CuratorUtils.clearRegistry(CuratorUtils.getZkClient());
            ThreadPoolFactoryUtils.shutdownAllThreadPools();
        }));
    }

}
