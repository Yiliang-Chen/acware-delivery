package top.acware.delivery.warning;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import top.acware.delivery.exception.DeliveryException;

import java.util.Arrays;
import java.util.Map;

@Slf4j
public class EmailWarning extends AbstractWarning {
    private final Email email;

    public EmailWarning(Map<?, ?> config, String subject, String[] to, String[] cc, String context) {
        if (config == null)
            throw new NullPointerException("Email global config is null");
        try {
            email = new HtmlEmail();
            email.setCharset((String) config.get("smtp.charset"));
            email.setHostName((String) config.get("smtp.hostname"));
            email.setAuthentication((String) config.get("smtp.authentication.username"), (String) config.get("smtp.authentication.password"));
            email.setFrom((String) config.get("smtp.from.email"), (String) config.get("smtp.from.name"));
            email.setSubject(subject).addTo(to).addCc(cc).setMsg(context);
            log.info("Set email [{}] warning email to {}, cc {}, msg = [{}]", subject, Arrays.toString(to), Arrays.toString(cc), context);
            execute();
        } catch (EmailException e) {
            log.error("Email message exception");
            throw new DeliveryException("Email message exception", e);
        }
    }

    @Override
    public void sendMessage() {
        try {
            email.send();
        } catch (EmailException e) {
            log.error("Email send exception");
            throw new DeliveryException("Email send exception", e);
        }
    }

}
