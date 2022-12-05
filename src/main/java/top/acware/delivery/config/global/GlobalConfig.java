package top.acware.delivery.config.global;

import java.util.Map;

public class GlobalConfig {

    public final Integer THREAD_CORE_POOL;
    public final Integer THREAD_MAX_POOL_SIZE;
    public final Long THREAD_KEEP_ALIVE_TIME;
    public final Integer THREAD_QUEUE_SIZE;

    public GlobalConfig(Map<?, ?> global) {
        Map<?, ?> threadPoolConfig = (Map<?, ?>) global.get("thread-pool");
        this.THREAD_CORE_POOL = (Integer) threadPoolConfig.get("core-pool.size");
        this.THREAD_MAX_POOL_SIZE = (Integer) threadPoolConfig.get("max-pool.size");
        this.THREAD_KEEP_ALIVE_TIME = Long.valueOf(threadPoolConfig.get("keep-alive.time").toString());
        this.THREAD_QUEUE_SIZE = (Integer) threadPoolConfig.get("queue.size");
    }

}
