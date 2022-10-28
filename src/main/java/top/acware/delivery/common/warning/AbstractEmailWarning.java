package top.acware.delivery.common.warning;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import top.acware.delivery.common.config.GlobalConfig;

public abstract class AbstractEmailWarning extends AbstractWarning {

    public abstract void addTo(String... toEmails);

    public abstract void addCc(String... ccEmails);

    public HtmlEmail emailClone(Email email) {
        HtmlEmail clone = new HtmlEmail();
        try {
            clone.setCharset(GlobalConfig.getInstance().getString(GlobalConfig.EMAIL_SMTP_CHARSET));
            clone.setHostName(GlobalConfig.getInstance().getString(GlobalConfig.EMAIL_SMTP_HOSTNAME));
            clone.setAuthentication(GlobalConfig.getInstance().getString(GlobalConfig.EMAIL_SMTP_AUTHENTICATION_USERNAME),
                    GlobalConfig.getInstance().getString(GlobalConfig.EMAIL_SMTP_AUTHENTICATION_PASSWORD));
            clone.setFrom(GlobalConfig.getInstance().getString(GlobalConfig.EMAIL_SMTP_FROM_EMAIL),
                    GlobalConfig.getInstance().getString(GlobalConfig.EMAIL_SMTP_FROM_NAME));
            clone.setSubject(email.getSubject());
            clone.setTo(email.getToAddresses());
            clone.setCc(email.getCcAddresses());
        } catch (EmailException e) {
            throw new RuntimeException(e);
        }
        return clone;
    }

}
