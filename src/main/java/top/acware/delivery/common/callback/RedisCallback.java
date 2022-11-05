package top.acware.delivery.common.callback;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import top.acware.delivery.common.config.DefaultConfig;
import top.acware.delivery.common.exception.ConfigException;
import top.acware.delivery.common.record.StringRecord;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class RedisCallback implements Callback<StringRecord>{

    // Record ID 对应 redis field
    private final AtomicLong recordId;
    // 当前消费位置
    private Long position;
    // Record version 对应 redis key
    private final String VERSION;
    // Record 保留数，小于 0 不删除
    private final int limit;
    private Long lastStatus;
    private final Lock readLock;
    private final JedisPool jedis;

    {
        this.recordId = new AtomicLong(0);
        this.position = 0L;
        this.readLock = new ReentrantLock();
    }

    public RedisCallback(String version) {
        this(version, DefaultConfig.DeliveryConfig.CALLBACK_LIMIT);
    }

    public RedisCallback(String version, Integer limit) {
        this(version, limit, DefaultConfig.DeliveryConfig.REDIS_HOST, DefaultConfig.DeliveryConfig.REDIS_PORT);
    }

    public RedisCallback(String version, Integer limit, String host, Integer port) {
        this.VERSION = version;
        this.limit = limit;
        if (limit >= 0) this.lastStatus = 0L;
        try {
            jedis = new JedisPool(host, port);
            log.info(" Connecting redis client {}:{}", host, port);
        } catch (Exception e) {
            log.error(" Redis init error ");
            e.printStackTrace();
            throw new ConfigException(" Redis init error ");
        }
    }

    @Override
    public StringRecord read() {
        try (Jedis redis = jedis.getResource()) {
            while (true) {
                if (redis.hexists(VERSION, String.valueOf(position)))
                    break;
            }
            return new StringRecord(redis.hget(VERSION, String.valueOf(position++)));
        } finally {
            log.debug(" Read end data, position++ = {}, recordId = {}", position, recordId.get());
            readLock.unlock();
        }
    }

    @Override
    public void write(StringRecord data) {
        while (limit > 0 && (recordId.get() - lastStatus) >= limit) {
            updateStatus();
        }
        try (Jedis redis = jedis.getResource()) {
            redis.hset(VERSION, String.valueOf(recordId.getAndIncrement()), data.getValue());
            log.debug(" Write data, position = {}, recordId = {}", position, recordId.get());
        }
    }

    @Override
    public boolean canRead() {
        return position < recordId.get() && readLock.tryLock();
    }

    @Override
    public void updateStatus() {
        readLock.lock();
        try(Jedis redis = jedis.getResource()) {
            for(Long i = lastStatus; i < position - lastStatus; i++) {
                if (redis.hexists(VERSION, String.valueOf(i)))
                    redis.hdel(VERSION, String.valueOf(i));
            }
        } finally {
            lastStatus = position;
            readLock.unlock();
        }
    }
}
