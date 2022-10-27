package top.acware.delivery.utils;

import top.acware.delivery.common.config.GlobalConfig;

import java.util.concurrent.*;

public class ThreadPool {

    private static ExecutorService executor;

    public static ExecutorService getExecutor() {
        if (executor == null || executor.isShutdown())
            init();
        return executor;
    }

    private synchronized static void init() {
        if (executor == null || executor.isShutdown()) {
            try {
                executor = new ThreadPoolExecutor(GlobalConfig.getInstance().getInt(GlobalConfig.THREAD_POOL_CORE_POLL_SIZE),
                        GlobalConfig.getInstance().getInt(GlobalConfig.THREAD_POOL_MAX_POLL_SIZE),
                        GlobalConfig.getInstance().getLong(GlobalConfig.THREAD_POOL_KEEP_ALIVE_TIME),
                        TimeUnit.MILLISECONDS,
                        (BlockingQueue<Runnable>) GlobalConfig.getInstance().getClass(GlobalConfig.THREAD_POOL_BLOCKING_QUEUE).newInstance()
                );
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
