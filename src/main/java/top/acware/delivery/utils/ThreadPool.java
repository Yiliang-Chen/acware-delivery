package top.acware.delivery.utils;


import top.acware.delivery.config.server.ServerConfig;

import java.util.concurrent.*;

/**
 * 线程池
 */
public class ThreadPool {

    private static ExecutorService executor;

    private static ExecutorService getExecutor() {
        if (executor == null || executor.isShutdown())
            init();
        return executor;
    }

    public static void executor(Runnable work) {
        getExecutor().execute(work);
    }

    private synchronized static void init() {
        if (executor == null || executor.isShutdown())
            executor = ServerConfig.global.THREAD_QUEUE_SIZE < 0 ? new ThreadPoolExecutor(
                    ServerConfig.global.THREAD_CORE_POOL,
                    ServerConfig.global.THREAD_MAX_POOL_SIZE,
                    ServerConfig.global.THREAD_KEEP_ALIVE_TIME,
                    TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>()
            ) : ServerConfig.global.THREAD_QUEUE_SIZE == 0 ? new ThreadPoolExecutor(
                    ServerConfig.global.THREAD_CORE_POOL,
                    ServerConfig.global.THREAD_MAX_POOL_SIZE,
                    ServerConfig.global.THREAD_KEEP_ALIVE_TIME,
                    TimeUnit.MILLISECONDS,
                    new SynchronousQueue<>()
            ) : new ThreadPoolExecutor(
                    ServerConfig.global.THREAD_CORE_POOL,
                    ServerConfig.global.THREAD_MAX_POOL_SIZE,
                    ServerConfig.global.THREAD_KEEP_ALIVE_TIME,
                    TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<>(ServerConfig.global.THREAD_QUEUE_SIZE)
            );
    }
}
