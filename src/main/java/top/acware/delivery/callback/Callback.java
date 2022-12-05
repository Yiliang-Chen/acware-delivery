package top.acware.delivery.callback;

import top.acware.delivery.record.Record;

/**
 * 回调函数基类
 */
public interface Callback {

    // 读
    Record read();

    // 写
    void write(Record data);

    // 是否可读
    boolean canRead();

    // 更新状态
    void updateStatus();

}
