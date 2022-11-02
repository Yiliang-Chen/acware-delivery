package top.acware.delivery.utils;

import top.acware.delivery.common.config.DefaultConfig;

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
            executor = DefaultConfig.DeliveryConfig.THREAD_POOL_QUEUE_SIZE < 0 ? new ThreadPoolExecutor(
                    DefaultConfig.DeliveryConfig.THREAD_POOL_CORE_POLL_SIZE,
                    DefaultConfig.DeliveryConfig.THREAD_POOL_MAX_POLL_SIZE,
                    DefaultConfig.DeliveryConfig.THREAD_POOL_KEEP_ALIVE_TIME,
                    TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>()
            ) : DefaultConfig.DeliveryConfig.THREAD_POOL_QUEUE_SIZE == 0 ? new ThreadPoolExecutor(
                    DefaultConfig.DeliveryConfig.THREAD_POOL_CORE_POLL_SIZE,
                    DefaultConfig.DeliveryConfig.THREAD_POOL_MAX_POLL_SIZE,
                    DefaultConfig.DeliveryConfig.THREAD_POOL_KEEP_ALIVE_TIME,
                    TimeUnit.MILLISECONDS,
                    new SynchronousQueue<>()
            ) : new ThreadPoolExecutor(
                    DefaultConfig.DeliveryConfig.THREAD_POOL_CORE_POLL_SIZE,
                    DefaultConfig.DeliveryConfig.THREAD_POOL_MAX_POLL_SIZE,
                    DefaultConfig.DeliveryConfig.THREAD_POOL_KEEP_ALIVE_TIME,
                    TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<>(DefaultConfig.DeliveryConfig.THREAD_POOL_QUEUE_SIZE)
            );
    }

}
