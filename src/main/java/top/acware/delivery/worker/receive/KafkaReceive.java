package top.acware.delivery.worker.receive;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import top.acware.delivery.callback.Callback;
import top.acware.delivery.record.KafkaRecord;
import top.acware.delivery.utils.ThreadPool;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

/**
 * 获取 kafka 数据
 */
@Slf4j
public class KafkaReceive extends AbstractReceive {

    private final KafkaConsumer<Object, Object> consumer;
    private final Long timeout;
    private final Callback callback;

    @SuppressWarnings("unchecked")
    public KafkaReceive(Callback callback, Map<?, ?> config) {
        super("KafkaReceive");
        consumer = new KafkaConsumer<Object, Object>((Map<String, Object>) config.get("properties"));
        // TODO 修改
        consumer.subscribe(Collections.singletonList((String) config.get("topic")));
        log.info("KafkaConsumer config [{}]", config.get("properties"));
        log.info("Subscribe topic [{}]", config.get("topic"));
        this.timeout = Long.parseLong(config.get("poll_timeout").toString());
        this.callback = callback;
    }

    @Override
    public void doWork() throws Exception {
        // 拉取数据
        for (ConsumerRecord<Object, Object> record : consumer.poll(Duration.ofMillis(timeout))) {
            // 写入 callback
            callback.write(new KafkaRecord(
                    record.topic(),
                    record.partition(),
                    record.offset(),
                    record.timestamp(),
                    record.key(),
                    record.value()
            ));
        }
    }

    @Override
    public void start() {
        ThreadPool.executor(this);
    }
}
