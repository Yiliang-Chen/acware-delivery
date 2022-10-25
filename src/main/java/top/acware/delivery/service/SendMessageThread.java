package top.acware.delivery.service;

import io.netty.channel.Channel;

public abstract class SendMessageThread extends CloseableThread{

    public final Channel channel;

    public SendMessageThread(Channel channel) {
        this("SendMessageThread - " + Thread.currentThread().getId(), channel);
    }

    public SendMessageThread(String threadName, Channel channel) {
        super(threadName);
        this.channel = channel;
    }

}
