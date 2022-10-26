package top.acware.delivery.common.warning;

public interface Warning {

    void setSubject(String subject);

    void addTo(String... toEmails);

    void addCc(String... ccEmails);

}
