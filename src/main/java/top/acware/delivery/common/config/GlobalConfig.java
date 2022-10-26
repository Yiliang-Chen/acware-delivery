package top.acware.delivery.common.config;

/**
 * Callback 相关配置
 */
public class GlobalConfig extends AbstractConfig{

    private static final ConfigDef CONFIG;

    private static final GlobalConfig instance;

    public static final String CALLBACK_LIMIT = "callback.limit";
    public static final String KAFKA_POLL_TIMEOUT = "kafka.poll-timeout";
    public static final String NETTY_MAX_CONTENT_LENGTH = "netty.max-content-length";
    public static final String EMAIL_SMTP_HOSTNAME = "email.smtp.hostname";
    public static final String EMAIL_SMTP_CHARSET = "email.smtp.charset";
    public static final String EMAIL_SMTP_AUTHENTICATION_USERNAME = "email.smtp.authentication.username";
    public static final String EMAIL_SMTP_AUTHENTICATION_PASSWORD = "email.smtp.authentication.password";
    public static final String EMAIL_SMTP_FROM_EMAIL = "email.smtp.from.email";
    public static final String EMAIL_SMTP_FROM_NAME = "email.smtp.from.name";

    static {
        CONFIG = new ConfigDef()
                .define(CALLBACK_LIMIT, ConfigDef.Type.INT)
                .define(KAFKA_POLL_TIMEOUT, ConfigDef.Type.LONG)
                .define(NETTY_MAX_CONTENT_LENGTH, ConfigDef.Type.INT)
                .define(EMAIL_SMTP_HOSTNAME, ConfigDef.Type.STRING)
                .define(EMAIL_SMTP_CHARSET, ConfigDef.Type.STRING)
                .define(EMAIL_SMTP_AUTHENTICATION_USERNAME, ConfigDef.Type.STRING)
                .define(EMAIL_SMTP_AUTHENTICATION_PASSWORD, ConfigDef.Type.STRING)
                .define(EMAIL_SMTP_FROM_EMAIL, ConfigDef.Type.STRING)
                .define(EMAIL_SMTP_FROM_NAME, ConfigDef.Type.STRING);
        instance = new GlobalConfig();
    }

    public GlobalConfig() {
        super(CONFIG);
    }

    public static GlobalConfig getInstance() {
        return instance;
    }
}
