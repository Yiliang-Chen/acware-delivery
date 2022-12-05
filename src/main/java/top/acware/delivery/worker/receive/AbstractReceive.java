package top.acware.delivery.worker.receive;

import top.acware.delivery.service.CloseableThread;

public abstract class AbstractReceive extends CloseableThread implements Receive{

    public AbstractReceive() {
        super("Receive");
    }

    public AbstractReceive(String threadName) {
        super(threadName);
    }

}
