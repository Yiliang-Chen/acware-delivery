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
    public static final String THREAD_POOL_CORE_POLL_SIZE = "thread.pool.core-pool-size";
    public static final String THREAD_POOL_MAX_POLL_SIZE = "thread.pool.max-pool-size";
    public static final String THREAD_POOL_KEEP_ALIVE_TIME = "thread.pool.keep-alive-time";
    public static final String THREAD_POOL_BLOCKING_QUEUE = "thread.pool.blocking-queue";
    public static final String HTTP_REQUEST_CHARSET = "http.request.charset";
    public static final String HTTP_REQUEST_TIMEOUT = "http.request.timeout";

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
                .define(EMAIL_SMTP_FROM_NAME, ConfigDef.Type.STRING)
                .define(THREAD_POOL_CORE_POLL_SIZE, ConfigDef.Type.INT)
                .define(THREAD_POOL_MAX_POLL_SIZE, ConfigDef.Type.INT)
                .define(THREAD_POOL_KEEP_ALIVE_TIME, ConfigDef.Type.LONG)
                .define(THREAD_POOL_BLOCKING_QUEUE, ConfigDef.Type.CLASS)
                .define(HTTP_REQUEST_CHARSET, ConfigDef.Type.STRING)
                .define(HTTP_REQUEST_TIMEOUT, ConfigDef.Type.INT);
        instance = new GlobalConfig();
    }

    public GlobalConfig() {
        super(CONFIG);
    }

    public static GlobalConfig getInstance() {
        return instance;
    }
}
