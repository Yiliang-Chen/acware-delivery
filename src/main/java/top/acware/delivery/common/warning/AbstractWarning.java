package top.acware.delivery.common.warning;

import top.acware.delivery.utils.ThreadPool;

/**
 * 告警消息基类，需要实现 sendMessage() 方法
 */
public abstract class AbstractWarning extends Thread{

    public abstract void sendMessage();

    /**
     * 使用线程池执行
     */
    public void execute() {
        ThreadPool.getExecutor().execute(this);
    }

    @Override
    public void run() {
        sendMessage();
    }

}
