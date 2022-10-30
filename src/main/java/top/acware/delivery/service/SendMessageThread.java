package top.acware.delivery.service;

import io.netty.channel.Channel;
import top.acware.delivery.common.callback.Callback;
import top.acware.delivery.common.record.Record;
import top.acware.delivery.common.warning.AbstractWarning;

import java.util.HashMap;
import java.util.Map;

public abstract class SendMessageThread extends CloseableThread{

    public Map<String, Channel> channels;
    public Callback<?> callback;
    public AbstractWarning warn;

    public SendMessageThread(Callback<?> callback) {
        this("SendMessageThread - " + Thread.currentThread().getId(), callback);
    }

    public SendMessageThread(String threadName, Callback<?> callback) {
        super(threadName);
        this.callback = callback;
    }

    public void setChannel(String key, Channel channel) {
        if (channels == null) {
            channels = new HashMap<>();
        }
        this.channels.put(key, channel);
    }

    public void addWarnMethod(AbstractWarning warn) {
        this.warn = warn;
    }

    public void warningRule(Record record) {}

    public abstract void work();

    @Override
    public void doWork() throws Exception {
        if (channels != null && !channels.isEmpty()) {
            work();
        }
    }
}
