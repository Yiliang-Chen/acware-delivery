package top.acware.delivery.handler.worker;

import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import top.acware.delivery.common.callback.Callback;
import top.acware.delivery.common.record.KafkaRecord;
import top.acware.delivery.common.record.Record;
import top.acware.delivery.common.warning.EmailWarning;
import top.acware.delivery.common.warning.HttpWarning;
import top.acware.delivery.service.SendMessageThread;
import top.acware.delivery.utils.HttpRequest;
import top.acware.delivery.utils.ThreadPool;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class KafkaSendMessageWorker<K, V> extends SendMessageThread {

    public KafkaSendMessageWorker(Callback<KafkaRecord<K,V>> callback) {
        super(callback);
    }

    public KafkaSendMessageWorker(String threadName, Callback<KafkaRecord<K,V>> callback) {
        super(threadName, callback);
    }

    @Override
    public void warningRule(Record record) {
//        EmailWarning emailWarning = (EmailWarning) warn;
//        try {
//            if (Long.parseLong((String) record.getValue()) < 0) {
//                emailWarning.setSubject("AcWare Delivery 负数告警");
//                emailWarning.setMsg("出现负数: " + record);
//                emailWarning.execute();
//            }
//        } catch (Exception e) {
//            log.warn(" Warning rule method error ");
//            emailWarning.setSubject("AcWare Delivery 数据告警");
//            emailWarning.setMsg("非法数据: " + record);
//            emailWarning.execute();
//        }

        HttpWarning httpWarning = (HttpWarning) warn;
        try {
            if (Long.parseLong((String) record.getValue()) < 0) {
                log.warn(" 出现负数，发起 HTTP 请求告警 ");
                String data = "search=abc";
                httpWarning.setData(data);
                httpWarning.execute();
            }
        } catch (Exception e) {
            log.warn(" Warning rule method error ");
        }
    }

    @Override
    public void work() {
        if (callback.canRead()) {
            KafkaRecord<K, V> record = (KafkaRecord<K, V>) callback.read();
            warningRule(record);
            log.debug(" Send message has {} channel ", channels.size());
            for (String key : channels.keySet()) {
                channels.get(key).writeAndFlush(new TextWebSocketFrame(record.toString()));
            }
        }
    }

}
