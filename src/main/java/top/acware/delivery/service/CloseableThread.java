package top.acware.delivery.service;

import lombok.extern.slf4j.Slf4j;

/**
 * 不断循环执行 doWork 方法
 */
@Slf4j
public abstract class CloseableThread extends Thread {

    private boolean running;

    private final String logIdent;

    private final boolean interrupt;

    public CloseableThread(String threadName) {
        this(threadName, false);
    }
    public CloseableThread(String threadName, boolean interrupt) {
        super(threadName);
        this.interrupt = interrupt;
        this.logIdent = "[" + getName() + " - " + getId() + "] ";
    }

    /**
     * 关闭线程
     */
    public void shutdown() {
        log.info(" {} Shutting down ", logIdent);
        running = false;
        if (interrupt)
            // 调用父类 interrupt 中断线程
            interrupt();
    }

    /**
     * 工作方法
     */
    public abstract void doWork() throws Exception;

    @Override
    public void run() {
        log.info(" {} Starting ", logIdent);
        running = true;
        try {
            while (running)
                doWork();
        } catch (Exception e) {
            log.error(" {} Error due to ", logIdent);
            throw new RuntimeException(e);
        }
        log.info(" {} Stopped ", logIdent);
    }
}
