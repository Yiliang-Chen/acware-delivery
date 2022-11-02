package top.acware.delivery.common.warning;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import top.acware.delivery.common.config.DefaultConfig;

/**
 * 邮件告警
 */
@Slf4j
public class EmailWarning extends AbstractWarning {
    private final Email email;
    private String msg;

    {
        try {
            email = new HtmlEmail();
            email.setCharset(DefaultConfig.DeliveryConfig.EMAIL_SMTP_CHARSET);
            email.setHostName(DefaultConfig.DeliveryConfig.EMAIL_SMTP_HOSTNAME);
            email.setAuthentication(DefaultConfig.DeliveryConfig.EMAIL_SMTP_AUTHENTICATION_USERNAME,
                    DefaultConfig.DeliveryConfig.EMAIL_SMTP_AUTHENTICATION_PASSWORD);
            email.setFrom(DefaultConfig.DeliveryConfig.EMAIL_SMTP_FROM_EMAIL,
                    DefaultConfig.DeliveryConfig.EMAIL_SMTP_FROM_NAME);
        } catch (EmailException e) {
            throw new RuntimeException(e);
        }
    }

    // 设置主题
    public void setSubject(Object subject) {
        email.setSubject((String) subject);
    }

    // 设置收件人
    public void addTo(String... toEmails) {
        try {
            email.addTo(toEmails);
        } catch (EmailException e) {
            throw new RuntimeException(e);
        }
    }

    // 设置抄送人
    public void addCc(String... ccEmails) {
        try {
            email.addCc(ccEmails);
        } catch (EmailException e) {
            throw new RuntimeException(e);
        }
    }

    // 设置消息
    public void setMsg(String msg) {
        this.msg = msg;
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

    // 每次 HtmlEmail 只能发送一条，需要克隆当前对象
    public HtmlEmail emailClone(Email email) {
        HtmlEmail clone = new HtmlEmail();
        try {
            clone.setCharset(DefaultConfig.DeliveryConfig.EMAIL_SMTP_CHARSET);
            clone.setHostName(DefaultConfig.DeliveryConfig.EMAIL_SMTP_HOSTNAME);
            clone.setAuthentication(DefaultConfig.DeliveryConfig.EMAIL_SMTP_AUTHENTICATION_USERNAME,
                    DefaultConfig.DeliveryConfig.EMAIL_SMTP_AUTHENTICATION_PASSWORD);
            clone.setFrom(DefaultConfig.DeliveryConfig.EMAIL_SMTP_FROM_EMAIL,
                    DefaultConfig.DeliveryConfig.EMAIL_SMTP_FROM_NAME);
            clone.setSubject(email.getSubject());
            clone.setTo(email.getToAddresses());
            clone.setCc(email.getCcAddresses());
        } catch (EmailException e) {
            throw new RuntimeException(e);
        }
        return clone;
    }

}
