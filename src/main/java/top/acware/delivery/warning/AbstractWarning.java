package top.acware.delivery.warning;

import top.acware.delivery.utils.ThreadPool;

/**
 * 告警消息基类，需要实现 sendMessage() 方法
 */
public abstract class AbstractWarning extends Thread implements Warning {

    /**
     * 直接执行
     */
    @Override
    public abstract void sendMessage();

    /**
     * 使用线程池执行
     */
    @Override
    public void execute() {
        ThreadPool.executor(this);
    }

    @Override
    public void run() {
        sendMessage();
    }

}