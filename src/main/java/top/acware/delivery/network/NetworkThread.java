package top.acware.delivery.network;

import lombok.extern.slf4j.Slf4j;

/**
 * 网络线程抽象类
 */
@Slf4j
public abstract class NetworkThread extends Thread implements Network{

    /**
     * 启动服务
     */
    public abstract void start();

    /**
     * 关闭服务
     */
    public abstract void serverShutdown();

    @Override
    public void run() {
        log.debug("NetworkThread starting ... {}", Thread.currentThread().getName());
        start();
    }

}
