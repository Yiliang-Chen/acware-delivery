package top.acware.delivery.common.exception;

/**
 * Builder 创建异常
 */
public class BuilderException extends DeliveryException{

    private static final long serialVersionUID = 1L;

    public BuilderException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public BuilderException(String msg) {
        super(msg);
    }

    public BuilderException(Throwable cause) {
        super(cause);
    }

    public BuilderException() {
        super();
    }

}
