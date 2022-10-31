package top.acware.delivery.handler.worker;

import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import top.acware.delivery.common.callback.Callback;
import top.acware.delivery.common.record.KafkaRecord;
import top.acware.delivery.service.SendMessageThread;

/**
 * Kafka 接收到的消息进行推送
 */
@Slf4j
public class KafkaSendMessageWorker<K, V> extends SendMessageThread {

    public KafkaSendMessageWorker(Callback<KafkaRecord<K,V>> callback) {
        super(callback);
    }

    public KafkaSendMessageWorker(String threadName, Callback<KafkaRecord<K,V>> callback) {
        super(threadName, callback);
    }

    @Override
    public void work() {
        if (callback.canRead()) {
            KafkaRecord<K, V> record = (KafkaRecord<K, V>) callback.read();
            warn.rule(record);
            log.debug(" Send message has {} channel ", channels.size());
            for (String key : channels.keySet()) {
                channels.get(key).writeAndFlush(new TextWebSocketFrame(record.toString()));
            }
        }
    }

}
