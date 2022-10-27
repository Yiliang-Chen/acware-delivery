package top.acware.delivery.common.warning;

public interface Warning {

    void setSubject(String subject);

    void setMessage(String msg);

    void setAndSendMessage(String msg);

    void sendMessage();

}
