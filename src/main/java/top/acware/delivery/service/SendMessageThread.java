package top.acware.delivery.service;

import io.netty.channel.Channel;
import top.acware.delivery.common.callback.Callback;
import top.acware.delivery.common.record.Record;
import top.acware.delivery.common.warning.Warning;

public abstract class SendMessageThread extends CloseableThread{

    public Channel channel;
    public Callback<?> callback;
    public Warning warn;

    public SendMessageThread(Callback<?> callback) {
        this("SendMessageThread - " + Thread.currentThread().getId(), callback);
    }

    public SendMessageThread(String threadName, Callback<?> callback) {
        super(threadName);
        this.callback = callback;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void addWarnMethod(Warning warn) {
        this.warn = warn;
    }

    public void warningRule(Record record) {}

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
