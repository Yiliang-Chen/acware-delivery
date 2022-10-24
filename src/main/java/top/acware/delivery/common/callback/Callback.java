package top.acware.delivery.common.callback;

public interface Callback<T> {

    T read();

    void write(T data);

    boolean canRead();

    boolean updateStatus();

}
