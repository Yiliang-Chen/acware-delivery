package top.acware.delivery.common;

import top.acware.delivery.common.DeliveryException;

public class ConfigException extends DeliveryException {

    private static final long serialVersionUID = 1L;

    public ConfigException(String msg) {
        super(msg);
    }

    public ConfigException(String name, Object value) {
        this(name, value, null);
    }

    public ConfigException(String name, Object value, String msg) {
        super(" Invalid value " + value + " for configuration " + name + (msg == null ? "" : ": " + msg));
    }

}
