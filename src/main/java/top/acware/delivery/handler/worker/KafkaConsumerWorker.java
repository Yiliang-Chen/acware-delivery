package top.acware.delivery.handler.worker;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import top.acware.delivery.common.callback.Callback;
import top.acware.delivery.common.config.GlobalConfig;
import top.acware.delivery.common.record.KafkaRecord;
import top.acware.delivery.service.CloseableThread;

public class KafkaConsumerWorker<K, V> extends CloseableThread {

    private final KafkaConsumer<K, V> consumer;

    private final Callback<KafkaRecord<K, V>> callback;

    private final Long poll_timeout;

    public KafkaConsumerWorker(KafkaConsumer<K, V> consumer, Callback<KafkaRecord<K, V>> callback) {
        this("KafkaConsumerWorker-" + Thread.currentThread().getId(), consumer, callback);
    }
    public KafkaConsumerWorker(String threadName, KafkaConsumer<K, V> consumer, Callback<KafkaRecord<K, V>> callback) {
        super(threadName);
        this.consumer = consumer;
        this.callback = callback;
        this.poll_timeout = GlobalConfig.getInstance().getLong(GlobalConfig.KAFKA_POLL_TIMEOUT);
    }

    @Override
    public void doWork() throws Exception {
        ConsumerRecords<K, V> records = consumer.poll(poll_timeout);
        for (ConsumerRecord<K, V> record : records) {
            callback.write(new KafkaRecord<K, V>(
                    record.topic(),
                    record.partition(),
                    record.offset(),
                    record.timestamp(),
                    record.key(),
                    record.value()
            ));
        }
    }
}
