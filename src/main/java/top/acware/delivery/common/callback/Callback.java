package top.acware.delivery.common.callback;

/**
 * 回调函数基类
 */
public interface Callback<T> {

    // 读
    T read();

    // 写
    void write(T data);

    // 是否可读
    boolean canRead();

    // 更新状态
    void updateStatus();

}
