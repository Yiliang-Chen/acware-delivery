package top.acware.delivery.service;

import io.netty.channel.Channel;

public abstract class SendMessageThread extends CloseableThread{

    public Channel channel;

    public SendMessageThread() {
        this("SendMessageThread - " + Thread.currentThread().getId());
    }

    public SendMessageThread(String threadName) {
        super(threadName);
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public abstract void work();

    @Override
    public void doWork() throws Exception {
        if (channel != null) {
            work();
        } else {
            throw new NullPointerException(" Channel is null, invoke setChannel method solve ");
        }
    }
}
