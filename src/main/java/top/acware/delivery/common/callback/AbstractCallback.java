package top.acware.delivery.common.callback;

import lombok.extern.slf4j.Slf4j;
import top.acware.delivery.common.config.GlobalConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class AbstractCallback<T> implements Callback<T> {

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
        limit = GlobalConfig.getInstance().getInt(GlobalConfig.CALLBACK_LIMIT);
    }

    @Override
    public boolean canRead() {
        if (position < mark.get())
            return true;
        return false;
    }

    @Override
    public boolean updateStatus() {
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
        if (!(list.size() < limit)) {
            log.info(" The current state cannot be update, position = {}, mark = {} ", position, mark.get());
            return false;
        }
        return true;
    }

    @Override
    public T read() {
        readLock.lock();
        try {
            log.debug(" Read data, position = {}, mark = {}", position, mark.get());
            return list.get(position);
        } finally {
            position++;
            readLock.unlock();
        }
    }

    @Override
    public void write(T data) {
        if (list.size() >= limit) {
            while (!updateStatus()) {
                log.info(" List over the limit, update status ");
            }
        }
        list.add(data);
        mark.incrementAndGet();
    }
}
