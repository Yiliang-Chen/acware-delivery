package top.acware.delivery.common.record;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Kafka 消息的部分内容封装
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class KafkaRecord<K, V> implements Record {

    private String topic;
    private int partition;
    private long offset;
    private long timestamp;
    private K key;
    private V value;

}
