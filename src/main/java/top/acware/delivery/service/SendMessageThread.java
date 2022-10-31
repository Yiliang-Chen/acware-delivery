package top.acware.delivery.service;

import io.netty.channel.Channel;
import top.acware.delivery.common.callback.Callback;
import top.acware.delivery.common.warning.WarnRule;

import java.util.HashMap;
import java.util.Map;

/**
 * 发送数据的线程基类
 */
public abstract class SendMessageThread extends CloseableThread{

    public Map<String, Channel> channels;
    public Callback<?> callback;
    public WarnRule warn;

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

    public void addWarnMethod(WarnRule warn) {
        this.warn = warn;
    }

    public abstract void work();

    @Override
    public void doWork() throws Exception {
        if (channels != null && !channels.isEmpty()) {
            work();
        }
    }
}
