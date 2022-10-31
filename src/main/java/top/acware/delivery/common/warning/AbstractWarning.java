package top.acware.delivery.common.warning;

import top.acware.delivery.utils.ThreadPool;

/**
 * 告警消息基类，需要实现 sendMessage() 方法
 */
public abstract class AbstractWarning extends Thread{

    public abstract void sendMessage();

    public void execute() {
        ThreadPool.getExecutor().execute(this);
    }

    @Override
    public void run() {
        sendMessage();
    }

}
