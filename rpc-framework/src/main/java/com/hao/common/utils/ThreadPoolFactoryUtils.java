package com.hao.common.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.hao.common.config.CustomThreadPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;

public final class ThreadPoolFactoryUtils {

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolFactoryUtils.class);


    private static final Map<String, ExecutorService> THREAD_POOLS = new ConcurrentHashMap<>();


    private ThreadPoolFactoryUtils() {}



    private static ExecutorService createThreadPool(CustomThreadPoolConfig customThreadPoolConfig, String threadNamePrefix, Boolean daemon) {
        ThreadFactory threadFactory = createThreadFactory(threadNamePrefix, daemon);
        return new ThreadPoolExecutor(customThreadPoolConfig.getCorePoolSize(), customThreadPoolConfig.getMaximumPoolSize(),
                customThreadPoolConfig.getKeepAliveTime(), customThreadPoolConfig.getUnit(), customThreadPoolConfig.getWorkQueue(),
                threadFactory);
    }



    /**
     * 创建 ThreadFactory 。如果threadNamePrefix不为空则使用自建ThreadFactory，否则使用defaultThreadFactory
     *
     * @param threadNamePrefix 作为创建的线程名字的前缀
     * @param daemon           指定是否为 Daemon Thread(守护线程)
     * @return ThreadFactory
     */
    public static ThreadFactory createThreadFactory(String threadNamePrefix, Boolean daemon) {
        if (threadNamePrefix != null) {
            if (daemon != null) {
                return new ThreadFactoryBuilder()
                        .setNameFormat(threadNamePrefix + "-%d")
                        .setDaemon(daemon).build();
            } else {
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").build();
            }
        }
        return Executors.defaultThreadFactory();
    }

    /**
     * 打印线程池的状态
     *
     * @param threadPool 线程池对象
     */
    public static void printThreadPoolStatus(ThreadPoolExecutor threadPool) {
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1, createThreadFactory("print-thread-pool-status", false));
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            logger.info("============ThreadPool Status=============");
            logger.info("ThreadPool Size: [{}]", threadPool.getPoolSize());
            logger.info("Active Threads: [{}]", threadPool.getActiveCount());
            logger.info("Number of Tasks : [{}]", threadPool.getCompletedTaskCount());
            logger.info("Number of Tasks in Queue: {}", threadPool.getQueue().size());
            logger.info("===========================================");
        }, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * 關閉
     */
    public static void shutdownAllThreadPools() {
        logger.info("call shutDownAllThreadPool method");
        THREAD_POOLS.entrySet().parallelStream().forEach(entry -> {
            ExecutorService executorService = entry.getValue();
            executorService.shutdown();
            logger.info("shut down thread pool [{}] [{}]", entry.getKey(), executorService.isTerminated());
            try {
                executorService.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.error("Thread pool never terminated");
                executorService.shutdownNow();
            }
        });
    }
}
