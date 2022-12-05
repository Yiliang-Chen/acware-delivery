package top.acware.delivery.record;

import java.util.Map;

public class MapRecord implements Record {

    private Map<?, ?> value;

    public MapRecord() {
    }

    public MapRecord(Map<?, ?> value) {
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
