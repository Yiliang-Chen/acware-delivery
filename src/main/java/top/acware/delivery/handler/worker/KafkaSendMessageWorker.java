package top.acware.delivery.handler.worker;

import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import top.acware.delivery.common.callback.Callback;
import top.acware.delivery.common.record.KafkaRecord;
import top.acware.delivery.service.SendMessageThread;

public class KafkaSendMessageWorker<K, V> extends SendMessageThread {

    private final Callback<KafkaRecord<K, V>> callback;

    public KafkaSendMessageWorker(Callback<KafkaRecord<K,V>> callback) {
        this.callback = callback;
    }

    public KafkaSendMessageWorker(String threadName, Callback<KafkaRecord<K,V>> callback) {
        super(threadName);
        this.callback = callback;
    }

    @Override
    public void work() {
        if (callback.canRead()) {
            channel.writeAndFlush(new TextWebSocketFrame((String) callback.read().getValue()));
        }
    }

}
