package top.acware.delivery.exception;

public class CallbackException extends DeliveryException{

    private static final long serialVersionUID = 1L;

    public CallbackException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public CallbackException(String msg) {
        super(msg);
    }

    public CallbackException(Throwable cause) {
        super(cause);
    }

    public CallbackException() {
        super();
    }

}
