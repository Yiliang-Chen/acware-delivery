package top.acware.delivery.worker.send;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import top.acware.delivery.callback.Callback;
import top.acware.delivery.config.warning.WarningConfig;
import top.acware.delivery.record.Record;
import top.acware.delivery.service.CloseableThread;
import top.acware.delivery.utils.CopyOnWriteMap;
import top.acware.delivery.warning.WarningRule;

@Slf4j
public class Sender extends CloseableThread {

    public final CopyOnWriteMap<String, Channel> channels;
    public final Callback callback;
    public final WarningRule warning;
    public boolean start = false;

    public Sender(Callback callback, WarningRule warning) {
        super("Sender");
        this.callback = callback;
        this.warning = warning;
        this.channels = new CopyOnWriteMap<>();
    }

    // 添加 channel
    public void setChannel(String key, Channel channel) {
        this.channels.put(key, channel);
    }

    public void work() {
        if (callback.canRead()) {
            Record record = callback.read();
            if (warning != null)
                warning.checkRecord(record);
            log.debug("Send message has {} channel", channels.size());
            for (String id : channels.keySet())
                channels.get(id).writeAndFlush(new TextWebSocketFrame(record.toJsonString()));
        }
    }

    @Override
    public void doWork() throws Exception {
        if (channels != null && !channels.isEmpty())
            work();
    }
}
