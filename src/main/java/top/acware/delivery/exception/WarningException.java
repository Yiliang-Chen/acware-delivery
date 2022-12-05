package top.acware.delivery.exception;

public class WarningException extends DeliveryException{

    private static final long serialVersionUID = 1L;

    public WarningException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public WarningException(String msg) {
        super(msg);
    }

    public WarningException(Throwable cause) {
        super(cause);
    }

    public WarningException() {
        super();
    }

}
