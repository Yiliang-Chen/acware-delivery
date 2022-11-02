package top.acware.delivery.example;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.Test;
import top.acware.delivery.common.callback.Callback;
import top.acware.delivery.common.callback.DefaultCallback;
import top.acware.delivery.common.record.KafkaRecord;
import top.acware.delivery.common.record.Record;
import top.acware.delivery.common.record.StringRecord;
import top.acware.delivery.common.warning.EmailWarning;
import top.acware.delivery.common.warning.HttpWarning;
import top.acware.delivery.common.warning.WarnRule;
import top.acware.delivery.handler.channel.DefaultChannelHandler;
import top.acware.delivery.handler.worker.*;
import top.acware.delivery.service.SendMessageThread;
import top.acware.delivery.utils.HttpRequest;
import top.acware.delivery.utils.ThreadPool;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class example {

    @Test
    public void http() {
        Callback<StringRecord> callback = new DefaultCallback<>();

        HttpWarning httpWarning = new HttpWarning();
        httpWarning.setUrl("https://www.baidu.com");
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        httpWarning.setHeaders(header);
        httpWarning.setMethod(HttpRequest.RequestMethod.GET);
        httpWarning.setToJson(false);

        WebsocketServerWorker ws = new WebsocketServerWorker.Builder()
                .defaultHandler(new DefaultChannelHandler(new HttpSendMessageWorker(callback), new WarnRule() {
                    @Override
                    public void rule(Record record) {
                        try {
                            if (Long.parseLong((String) record.getValue()) < 0) {
                                String data = "search=abc";
                                httpWarning.setData(data);
                                httpWarning.execute();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }))
                .bossThreads(1)
                .websocketPath("/ws")
                .inetPort(9999)
                .build()
                .setDefaultHandler()
                .setDefaultChildHandler();

        HttpReceiveWorker receive = new HttpReceiveWorker.Builder()
                .bossThreads(1)
                .inetPort(9998)
                .method(HttpMethod.POST)
                .uri("/data")
                .callback(callback)
                .builder()
                .setDefaultChildHandler();
        ThreadPool.executor(ws);
        ThreadPool.executor(receive);
        try {
            System.in.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void kafka() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "example");
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 1000);
        properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Collections.singletonList("kafka"));
        Callback<KafkaRecord<String, String>> callback = new DefaultCallback<>();
        KafkaConsumerWorker<String, String> worker = new KafkaConsumerWorker<>(consumer, callback);
        ThreadPool.executor(worker);

        /* Email Warning */
        EmailWarning emailWarning = new EmailWarning();
        emailWarning.addTo("AcWare-Delivery-Admin@acware.top");
        emailWarning.addCc("AcWare-Delivery-Warn@acware.top");

        WebsocketServerWorker ws = new WebsocketServerWorker.Builder()
                .websocketPath("/ws")
                .inetPort(9999)
                .defaultHandler(new DefaultChannelHandler(new KafkaSendMessageWorker<>(callback), new WarnRule() {
                    @Override
                    public void rule(Record record) {
                        try {
                            if (Long.parseLong((String) record.getValue()) < 0) {
                                emailWarning.setSubject("AcWare Delivery 负数告警");
                                emailWarning.setMsg("出现负数: " + record);
                                emailWarning.execute();
                            }
                        } catch (Exception e) {
                            emailWarning.setSubject("AcWare Delivery 数据告警");
                            emailWarning.setMsg("非法数据: " + record);
                            emailWarning.execute();
                        }
                    }
                }))
                .bossThreads(1)
                .build()
                .setDefaultHandler()
                .setDefaultChildHandler();
        ThreadPool.executor(ws);

        try {
            System.in.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void define() {

        class send extends SendMessageThread{
            public send(Callback<?> callback) {
                super(callback);
            }

            @Override
            public void work() {
                if (callback.canRead()) {
                    StringRecord record = (StringRecord) callback.read();
                    warn.rule(record);
                    for (String key : channels.keySet()) {
                        channels.get(key).writeAndFlush(new TextWebSocketFrame(record.getValue()));
                    }
                }
            }
        }

        Callback<StringRecord> callback = new DefaultCallback<>();
        HttpWarning httpWarning = new HttpWarning();
        httpWarning.setUrl("https://www.baidu.com");
        WebsocketServerWorker ws = new WebsocketServerWorker.Builder()
                .websocketPath("/ws")
                .inetPort(9999)
                .bossThreads(1)
                .defaultHandler(new DefaultChannelHandler(new send(callback), new WarnRule() {
                    @Override
                    public void rule(Record record) {
                        httpWarning.sendMessage();
                    }
                }))
                .build()
                .setDefaultHandler()
                .setDefaultChildHandler();
        ThreadPool.executor(ws);

        HttpReceiveWorker receive = new HttpReceiveWorker.Builder()
                .uri("/data")
                .method(HttpMethod.POST)
                .callback(callback)
                .inetPort(9998)
                .builder()
                .setDefaultHandler()
                .setDefaultChildHandler();
        ThreadPool.executor(receive);

        try {
            System.in.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
