package top.acware.delivery.common.warning;

import top.acware.delivery.utils.ThreadPool;


public abstract class AbstractWarning extends Thread implements Warning{

    public String msg;

    @Override
    public void setMessage(String msg) {
        this.msg = msg;
    }

    @Override
    public abstract void sendMessage();

    @Override
    public void setAndSendMessage(String msg) {
        this.msg = msg;
        ThreadPool.getExecutor().execute(this);
    }

    @Override
    public void run() {
        sendMessage();
    }
}
