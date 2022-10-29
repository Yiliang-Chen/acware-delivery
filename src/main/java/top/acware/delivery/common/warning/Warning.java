package top.acware.delivery.common.warning;

public interface Warning {

    void setSubject(Object subject);

    void setMessage(Object msg);

    void setAndSendMessage(Object msg);

    void sendMessage();

}
