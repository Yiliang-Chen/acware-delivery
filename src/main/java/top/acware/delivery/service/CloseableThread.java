package top.acware.delivery.service;

import lombok.extern.slf4j.Slf4j;
import top.acware.delivery.exception.DeliveryException;

/**
 * 不断循环执行 doWork 方法
 */
@Slf4j
public abstract class CloseableThread extends Thread{

    private boolean running;
    private final String logIdent;

    public CloseableThread() {
        this("CloseableThread");
    }

    public CloseableThread(String threadName) {
        super(threadName);
        this.logIdent = "[" + getName() + "-" + getId() + "]";
    }

    /**
     * 关闭线程
     */
    public void shutdown() {
        log.info("{} Shutting down", logIdent);
        running = false;
    }

    /**
     * 工作方法
     */
    public abstract void doWork() throws Exception;

    @Override
    public void run() {
        log.info("{} Starting", logIdent);
        running = true;
        try {
            while (running)
                doWork();
        } catch (Exception e) {
            log.error("{} Error due to", logIdent);
            throw new DeliveryException(e);
        }
        log.info("{} Stopped", logIdent);
    }
}
