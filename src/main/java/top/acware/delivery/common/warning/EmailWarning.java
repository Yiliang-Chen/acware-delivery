package top.acware.delivery.common.warning;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import top.acware.delivery.common.config.GlobalConfig;

@Slf4j
public class EmailWarning extends AbstractEmailWarning {
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
    public void setSubject(String subject) {
        email.setSubject(subject);
    }

    @Override
    public void addTo(String... toEmails) {
        try {
            email.addTo(toEmails);
        } catch (EmailException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
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
            clone.setMsg(msg).send();
            log.info("Send [{}] warning email to {}, cc {}, msg = [{}]", email.getSubject(), email.getToAddresses(), email.getCcAddresses(), msg);
        } catch (EmailException e) {
            throw new RuntimeException(e);
        }
    }

}
