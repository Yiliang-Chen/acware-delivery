package top.acware.delivery.handler.worker;

import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import top.acware.delivery.common.callback.Callback;
import top.acware.delivery.common.record.KafkaRecord;
import top.acware.delivery.common.record.Record;
import top.acware.delivery.service.SendMessageThread;

public class KafkaSendMessageWorker<K, V> extends SendMessageThread {

    public KafkaSendMessageWorker(Callback<KafkaRecord<K,V>> callback) {
        super(callback);
    }

    public KafkaSendMessageWorker(String threadName, Callback<KafkaRecord<K,V>> callback) {
        super(threadName, callback);
    }

    @Override
    public void warningRule(Record record) {
        if (Long.parseLong((String) record.getValue()) < 0) {
            warn.setSubject("AcWare Delivery 负数告警");
            this.warn.setAndSendMessage("出现负数: " + record);
        }
    }

    @Override
    public void work() {
        if (callback.canRead()) {
            KafkaRecord<K, V> record = (KafkaRecord<K, V>) callback.read();
            warningRule(record);
            for (String key : channels.keySet()) {
                channels.get(key).writeAndFlush(new TextWebSocketFrame(record.toString()));
            }
        }
    }

}
