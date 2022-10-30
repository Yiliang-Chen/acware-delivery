package top.acware.delivery.example;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import top.acware.delivery.common.callback.DefaultCallback;
import top.acware.delivery.common.callback.Callback;
import top.acware.delivery.common.record.KafkaRecord;
import top.acware.delivery.common.warning.EmailWarning;
import top.acware.delivery.common.warning.HttpWarning;
import top.acware.delivery.handler.channel.DefaultChannelHandler;
import top.acware.delivery.handler.worker.KafkaConsumerWorker;
import top.acware.delivery.handler.worker.KafkaSendMessageWorker;
import top.acware.delivery.network.WebsocketNetworkServer;
import top.acware.delivery.utils.HttpRequest;
import top.acware.delivery.utils.ThreadPool;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class KafkaTest {

    public static void main(String[] args) {
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
        ThreadPool.getExecutor().execute(worker);

        /* Email Warning */
//        EmailWarning emailWarning = new EmailWarning();
//        emailWarning.addTo("18177410488@163.com");
//        emailWarning.addCc("1982455737@qq.com");

        HttpWarning httpWarning = new HttpWarning();
        httpWarning.setUrl("https://www.baidu.com");
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        httpWarning.setHeaders(header);
        httpWarning.setMethod(HttpRequest.RequestMethod.GET);
        httpWarning.setToJson(false);

        WebsocketNetworkServer ws = new WebsocketNetworkServer.Builder()
                .websocketPath("/ws")
                .inetPort(9999)
                .defaultHandler(new DefaultChannelHandler(new KafkaSendMessageWorker<>(callback), httpWarning))
//                .defaultHandler(new DefaultChannelHandler(new KafkaSendMessageWorker<>(callback), emailWarning))
                .build()
                .createDefaultServer();
        ws.start();
        try {
            System.in.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
