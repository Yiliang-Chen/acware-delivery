package top.acware.delivery.common.exception;

/**
 * 所有 Delivery 异常基类
 */
public class DeliveryException extends RuntimeException{

    private final static long serialVersionUID = 1L;

    public DeliveryException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public DeliveryException(String msg) {
        super(msg);
    }

    public DeliveryException(Throwable cause) {
        super(cause);
    }

    public DeliveryException() {
        super();
    }

}
