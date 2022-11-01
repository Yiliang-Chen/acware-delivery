package top.acware.delivery.service;

import io.netty.channel.Channel;
import top.acware.delivery.common.callback.Callback;
import top.acware.delivery.common.warning.WarnRule;
import top.acware.delivery.utils.CopyOnWriteMap;

/**
 * 发送数据的线程基类
 */
public abstract class SendMessageThread extends CloseableThread{

    public CopyOnWriteMap<String, Channel> channels;
    public Callback<?> callback;
    public WarnRule warn;

    public SendMessageThread(Callback<?> callback) {
        this("SendMessageThread - " + Thread.currentThread().getId(), callback);
    }

    public SendMessageThread(String threadName, Callback<?> callback) {
        super(threadName);
        this.callback = callback;
    }

    /* 设置 channel */
    public void setChannel(String key, Channel channel) {
        if (channels == null) {
            channels = new CopyOnWriteMap<>();
        }
        this.channels.put(key, channel);
    }

    /* 添加告警方法 */
    public void addWarnMethod(WarnRule warn) {
        this.warn = warn;
    }

    /* 主要工作类 */
    public abstract void work();

    @Override
    public void doWork() throws Exception {
        if (channels != null && !channels.isEmpty()) {
            work();
        }
    }
}
