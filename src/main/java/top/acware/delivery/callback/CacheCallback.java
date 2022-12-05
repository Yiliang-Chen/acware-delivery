package top.acware.delivery.callback;

import lombok.extern.slf4j.Slf4j;
import top.acware.delivery.record.Record;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 将消息缓存到 cache
 */
@Slf4j
public class CacheCallback implements Callback {

    // 当前消费位置
    private int position = 0;
    // 可读最高位
    private final AtomicInteger mark = new AtomicInteger(0);
    // 限制位置
    private final Long LIMIT;
    // 读锁
    private final Lock readLock = new ReentrantLock();
    // 写阻塞状态
    private boolean blocking = false;
    // 数据缓存
    private final List<Record> list = new ArrayList<>();

    public CacheCallback(Long limit) {
        this.LIMIT = limit;
    }

    /**
     * 判断是否可读
     */
    @Override
    public boolean canRead() {
        return position < mark.get() && readLock.tryLock();
    }

    /**
     * 修改状态，修改期间不允许读
     */
    @Override
    public void updateStatus() {
        readLock.lock();
        log.debug("Update status start, position = {}, mark = {}", position, mark.get());
        try {
            if (position > 0) {
                list.subList(0, position).clear();
            }
            mark.set(list.size());
            position = 0;
        } finally {
            log.debug("Update status end, position = {}, mark = {}", position, mark.get());
            readLock.unlock();
        }
    }

    /**
     * 读取数据，读取期间不允许修改状态
     */
    @Override
    public Record read() {
        try {
            log.debug("Read data, position = {}, mark = {}", position, mark.get());
            return list.get(position);
        } finally {
            position++;
            readLock.unlock();
            if (blocking && position == mark.get()) {
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

    /**
     * 写数据
     */
    @Override
    public void write(Record data) {
        while (list.size() >= LIMIT) {
            updateStatus();
            if (list.size() >= LIMIT) {
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
        list.add(data);
        log.debug("Add data -> {} ", data);
        mark.incrementAndGet();
    }
}
