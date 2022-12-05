package top.acware.delivery.record;

public class ObjectRecord implements Record {

    private Object value;

    public ObjectRecord() {
    }

    public ObjectRecord(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return toJsonString();
    }

    @Override
    public String toJsonString() {
        return "{\"value\":\"" + value + "\"}";
    }

    @Override
    public Object getValue() {
        return value;
    }
}
