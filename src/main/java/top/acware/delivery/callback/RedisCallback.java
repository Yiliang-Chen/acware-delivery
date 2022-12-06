package top.acware.delivery.callback;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import top.acware.delivery.record.ObjectRecord;
import top.acware.delivery.record.Record;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class RedisCallback implements Callback{

    // 当前消费位置
    private Long position = 0L;
    // 更新前最后位置
    private Long last = 0L;
    // 写阻塞状态
    private boolean blocking = false;
    // redis id
    private final AtomicLong offset = new AtomicLong(0);
    // redis key
    private final String VERSION;
    // record 保留数，小于等于 0 不删除
    private final Long LIMIT;
    // redis pool
    private final JedisPool jedis;
    // 读锁
    private final Lock readLock = new ReentrantLock();

    public RedisCallback(Long limit, Map<?, ?> config) {
        this.LIMIT = limit;
        this.VERSION = (String) config.get("version");
        this.jedis = new JedisPool((String) config.get("host"), Integer.parseInt(config.get("port").toString()));
        log.info("Connecting redis client {}:{}", config.get("host"), config.get("port"));
    }

    @Override
    public Record read() {
        try (Jedis redis = jedis.getResource()){
            while (true) {
                if (redis.hexists(VERSION, String.valueOf(position)))
                    break;
            }
            return new ObjectRecord(redis.hget(VERSION, String.valueOf(position++)));
        } finally {
            log.debug("Read end data, position = {}, offset = {}", position, offset.get());
            readLock.unlock();
            if (blocking && position == offset.get()) {
                synchronized (readLock) {
                    if (blocking) {
                        log.info("Notify blocking thread");
                        readLock.notify();
                        blocking = false;
                    }
                }
            }
        }
    }

    @Override
    public void write(Record data) {
        while (LIMIT > 0 && (offset.get() - last) >= LIMIT) {
            updateStatus();
            if ((offset.get() - last) >= LIMIT) {
                synchronized (readLock) {
                    try {
                        log.warn("Callback List over the limit, wakeup waiting");
                        blocking = true;
                        readLock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        try (Jedis redis = jedis.getResource()) {
            redis.hset(VERSION, String.valueOf(offset.getAndIncrement()), data.toJsonString());
            log.debug("Write data, position = {}, offset = {}", position, offset.get());
            log.debug("Add data -> {} ", data);
        }
    }

    @Override
    public boolean canRead() {
        return position < offset.get() && readLock.tryLock();
    }

    @Override
    public void updateStatus() {
        readLock.lock();
        log.debug("Update status start, last = {}, position = {}", last, position);
        try (Jedis redis = jedis.getResource()) {
            for (Long i = last; i < position - last; i++)
                if (redis.hexists(VERSION, String.valueOf(i)))
                    redis.hdel(VERSION, String.valueOf(i));
            last = position;
        } finally {
            log.debug("Update status end, last = {}, position = {}", last, position);
            readLock.unlock();
        }
    }
}
