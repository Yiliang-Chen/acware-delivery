package top.acware.delivery.common.warning;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import top.acware.delivery.common.config.GlobalConfig;

@Slf4j
public class EmailWarning extends AbstractWarning {
    private final Email email;

    {
        try {
            email = new HtmlEmail();
            email.setCharset(GlobalConfig.getInstance().getString(GlobalConfig.EMAIL_SMTP_CHARSET));
            email.setHostName(GlobalConfig.getInstance().getString(GlobalConfig.EMAIL_SMTP_HOSTNAME));
            email.setAuthentication(GlobalConfig.getInstance().getString(GlobalConfig.EMAIL_SMTP_AUTHENTICATION_USERNAME),
                    GlobalConfig.getInstance().getString(GlobalConfig.EMAIL_SMTP_AUTHENTICATION_PASSWORD));
            email.setFrom(GlobalConfig.getInstance().getString(GlobalConfig.EMAIL_SMTP_FROM_EMAIL),
                    GlobalConfig.getInstance().getString(GlobalConfig.EMAIL_SMTP_FROM_NAME));
        } catch (EmailException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setSubject(Object subject) {
        email.setSubject((String) subject);
    }

    public void addTo(String... toEmails) {
        try {
            email.addTo(toEmails);
        } catch (EmailException e) {
            throw new RuntimeException(e);
        }
    }

    public void addCc(String... ccEmails) {
        try {
            email.addCc(ccEmails);
        } catch (EmailException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendMessage() {
        try {
            HtmlEmail clone = emailClone(email);
            clone.setMsg((String) msg).send();
            log.info("Send [{}] warning email to {}, cc {}, msg = [{}]", email.getSubject(), email.getToAddresses(), email.getCcAddresses(), msg);
        } catch (EmailException e) {
            throw new RuntimeException(e);
        }
    }

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
