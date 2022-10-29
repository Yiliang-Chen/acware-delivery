package top.acware.delivery.common.warning;

import top.acware.delivery.utils.ThreadPool;

public abstract class AbstractWarning extends Thread implements Warning{

    public Object msg;

    @Override
    public void setMessage(Object msg) {
        this.msg = msg;
    }

    @Override
    public void setAndSendMessage(Object msg) {
        this.msg = msg;
        ThreadPool.getExecutor().execute(this);
    }

    @Override
    public void run() {
        sendMessage();
    }
}
