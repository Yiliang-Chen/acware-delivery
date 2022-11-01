package top.acware.delivery.service;

import lombok.extern.slf4j.Slf4j;

/**
 * 工作线程基类
 */
@Slf4j
public abstract class WorkerThread extends Thread {

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
        log.debug(" WorkerThread {} start ", Thread.currentThread().getName());
        start();
    }

}
