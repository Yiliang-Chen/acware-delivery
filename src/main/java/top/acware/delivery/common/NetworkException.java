package top.acware.delivery.common;

public class NetworkException extends DeliveryException {

    private static final long serialVersionUID = 1L;

    public NetworkException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public NetworkException(String msg) {
        super(msg);
    }

    public NetworkException(Throwable cause) {
        super(cause);
    }

    public NetworkException() {
        super();
    }

}
