package top.acware.delivery.record;

/**
 * Kafka 消息的部分内容封装
 */
public class KafkaRecord implements Record {

    private String topic;
    private int partition;
    private long offset;
    private long timestamp;
    private Object key;
    private Object value;

    public KafkaRecord() {
    }

    public KafkaRecord(String topic, int partition, long offset, long timestamp, Object key, Object value) {
        this.topic = topic;
        this.partition = partition;
        this.offset = offset;
        this.timestamp = timestamp;
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return toJsonString();
    }

    @Override
    public String toJsonString() {
        return "{\"topic\":\"" + topic +
                "\", \"partition\":\"" + partition +
                "\", \"offset\":\"" + offset +
                "\", \"timestamp\":\"" + timestamp +
                "\", \"key\":\"" + key +
                "\", \"value\":\"" + value +
                "\"}";
    }

    public String getTopic() {
        return topic;
    }

    public int getPartition() {
        return partition;
    }

    public long getOffset() {
        return offset;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Object getKey() {
        return key;
    }

    @Override
    public Object getValue() {
        return value;
    }
}