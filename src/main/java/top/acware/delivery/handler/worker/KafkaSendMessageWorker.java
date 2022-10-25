package top.acware.delivery.handler.worker;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import top.acware.delivery.common.callback.Callback;
import top.acware.delivery.common.record.KafkaRecord;
import top.acware.delivery.service.SendMessageThread;

public class KafkaSendMessageWorker<K, V> extends SendMessageThread {

    private final Callback<KafkaRecord<K, V>> callback;

    public KafkaSendMessageWorker(Channel channel, Callback<KafkaRecord<K,V>> callback) {
        super(channel);
        this.callback = callback;
    }

    @Override
    public void doWork() throws Exception {
        if (callback.canRead()) {
            channel.writeAndFlush(new TextWebSocketFrame((String) callback.read().getValue()));
        }
    }

}
