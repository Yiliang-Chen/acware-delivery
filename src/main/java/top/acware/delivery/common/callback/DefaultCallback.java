package top.acware.delivery.common.callback;

import lombok.extern.slf4j.Slf4j;
import top.acware.delivery.common.config.DefaultConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class DefaultCallback<T> implements Callback<T> {

    // 当前消费位置
    private int position = 0;
    // 可读最高位
    private final AtomicInteger mark;
    // 限制位置
    private final int limit;
    // 读锁
    private final Lock readLock;
    // 数据缓存
    private final List<T> list;

    {
        mark = new AtomicInteger(0);
        readLock = new ReentrantLock();
        list = new ArrayList<>();
    }

    public DefaultCallback() {
        this.limit = DefaultConfig.DeliveryConfig.CALLBACK_LIMIT;
    }

    public DefaultCallback(Integer limit) {
        this.limit = limit;
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
        log.debug(" Update status start, position = {}, mark = {} ", position, mark.get());
        try {
            for (int i = 0; i < position; i++) {
                list.remove(0);
            }
            mark.set(list.size());
            position = 0;
        } finally {
            log.debug(" Update status end, position = {}, mark = {} ", position, mark.get());
            readLock.unlock();
        }
    }

    /**
     * 读取数据，读取期间不允许修改状态
     */
    @Override
    public T read() {
        try {
            log.debug(" Read data, position = {}, mark = {}", position, mark.get());
            return list.get(position);
        } finally {
            position++;
            readLock.unlock();
        }
    }

    /**
     * 写数据
     */
    @Override
    public void write(T data) {
        while (list.size() >= limit) {
            updateStatus();
            log.debug(" List over the limit, update status ");
        }
        list.add(data);
        log.debug(" Add data -> {} ", data);
        mark.incrementAndGet();
    }
}
