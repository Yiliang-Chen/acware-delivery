package top.acware.delivery.common.warning;

import top.acware.delivery.utils.ThreadPool;

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
