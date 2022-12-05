package top.acware.delivery.exception;

public class ConfigException extends DeliveryException{

    private static final long serialVersionUID = 1L;

    public ConfigException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public ConfigException(String msg) {
        super(msg);
    }

    public ConfigException(Throwable cause) {
        super(cause);
    }

    public ConfigException() {
        super();
    }

}
