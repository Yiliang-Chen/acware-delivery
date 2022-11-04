package top.acware.delivery.common.config;

/**
 * 相关配置
 */
public class DefaultConfig extends AbstractConfig{

    private static final ConfigDef CONFIG;

    private static final DefaultConfig INSTANCE;

    /** Callback 配置 */
    public static final String CALLBACK_LIMIT_Prop = "callback.limit";

    /** Kafka 配置 */
    public static final String KAFKA_POLL_TIMEOUT_Prop = "kafka.poll-timeout";

    /** Netty 配置 */
    public static final String NETTY_MAX_CONTENT_LENGTH_Prop = "netty.max-content-length";

    /** Email 配置 */
    public static final String EMAIL_SMTP_HOSTNAME_Prop = "email.smtp.hostname";
    public static final String EMAIL_SMTP_CHARSET_Prop = "email.smtp.charset";
    public static final String EMAIL_SMTP_AUTHENTICATION_USERNAME_Prop = "email.smtp.authentication.username";
    public static final String EMAIL_SMTP_AUTHENTICATION_PASSWORD_Prop = "email.smtp.authentication.password";
    public static final String EMAIL_SMTP_FROM_EMAIL_Prop = "email.smtp.from.email";
    public static final String EMAIL_SMTP_FROM_NAME_Prop = "email.smtp.from.name";

    /** 线程池配置 */
    public static final String THREAD_POOL_CORE_POLL_SIZE_Prop = "thread.pool.core-pool-size";
    public static final String THREAD_POOL_MAX_POLL_SIZE_Prop = "thread.pool.max-pool-size";
    public static final String THREAD_POOL_KEEP_ALIVE_TIME_Prop = "thread.pool.keep-alive-time";
    public static final String THREAD_POOL_QUEUE_SIZE_Prop = "thread.pool.queue-size";

    /** Http 请求配置 */
    public static final String HTTP_REQUEST_CHARSET_Prop = "http.request.charset";
    public static final String HTTP_REQUEST_TIMEOUT_Prop = "http.request.timeout";

    /** Redis 配置 */
    public static final String REDIS_HOST_Prop = "redis.host";
    public static final String REDIS_PORT_Prop = "redis.port";

    static {
        CONFIG = new ConfigDef()
                .define(CALLBACK_LIMIT_Prop, ConfigDef.Type.INT, 5)
                .define(KAFKA_POLL_TIMEOUT_Prop, ConfigDef.Type.LONG, 1000)
                .define(NETTY_MAX_CONTENT_LENGTH_Prop, ConfigDef.Type.INT, 8192)
                .define(EMAIL_SMTP_HOSTNAME_Prop, ConfigDef.Type.STRING, "")
                .define(EMAIL_SMTP_CHARSET_Prop, ConfigDef.Type.STRING, "UTF-8")
                .define(EMAIL_SMTP_AUTHENTICATION_USERNAME_Prop, ConfigDef.Type.STRING, "")
                .define(EMAIL_SMTP_AUTHENTICATION_PASSWORD_Prop, ConfigDef.Type.STRING, "")
                .define(EMAIL_SMTP_FROM_EMAIL_Prop, ConfigDef.Type.STRING, "")
                .define(EMAIL_SMTP_FROM_NAME_Prop, ConfigDef.Type.STRING, "")
                .define(THREAD_POOL_CORE_POLL_SIZE_Prop, ConfigDef.Type.INT, 6)
                .define(THREAD_POOL_MAX_POLL_SIZE_Prop, ConfigDef.Type.INT, 18)
                .define(THREAD_POOL_KEEP_ALIVE_TIME_Prop, ConfigDef.Type.LONG, 200)
                .define(THREAD_POOL_QUEUE_SIZE_Prop, ConfigDef.Type.INT, -1)
                .define(HTTP_REQUEST_CHARSET_Prop, ConfigDef.Type.STRING, "UTF-8")
                .define(HTTP_REQUEST_TIMEOUT_Prop, ConfigDef.Type.INT, 60000)
                .define(REDIS_HOST_Prop, ConfigDef.Type.STRING, "localhost")
                .define(REDIS_PORT_Prop, ConfigDef.Type.INT, 6379);
        INSTANCE = new DefaultConfig();
    }

    private DefaultConfig() {
        super(CONFIG);
    }

    public static DefaultConfig getInstance() {
        return INSTANCE;
    }

    public static ConfigDef getConfig() {
        return CONFIG;
    }

    public static class DeliveryConfig {
        /** Callback 配置 */
        public static final Integer CALLBACK_LIMIT = getInstance().getInt(DefaultConfig.CALLBACK_LIMIT_Prop);

        /** Kafka 配置 */
        public static final Long KAFKA_POLL_TIMEOUT = getInstance().getLong(DefaultConfig.KAFKA_POLL_TIMEOUT_Prop);

        /** Netty 配置 */
        public static final Integer NETTY_MAX_CONTENT_LENGTH = getInstance().getInt(DefaultConfig.NETTY_MAX_CONTENT_LENGTH_Prop);

        /** Email 配置 */
        public static final String EMAIL_SMTP_HOSTNAME = getInstance().getString(DefaultConfig.EMAIL_SMTP_HOSTNAME_Prop);
        public static final String EMAIL_SMTP_CHARSET = getInstance().getString(DefaultConfig.EMAIL_SMTP_CHARSET_Prop);
        public static final String EMAIL_SMTP_AUTHENTICATION_USERNAME = getInstance().getString(DefaultConfig.EMAIL_SMTP_AUTHENTICATION_USERNAME_Prop);
        public static final String EMAIL_SMTP_AUTHENTICATION_PASSWORD = getInstance().getString(DefaultConfig.EMAIL_SMTP_AUTHENTICATION_PASSWORD_Prop);
        public static final String EMAIL_SMTP_FROM_EMAIL = getInstance().getString(DefaultConfig.EMAIL_SMTP_FROM_EMAIL_Prop);
        public static final String EMAIL_SMTP_FROM_NAME = getInstance().getString(DefaultConfig.EMAIL_SMTP_FROM_EMAIL_Prop);

        /** 线程池配置 */
        public static final Integer THREAD_POOL_CORE_POLL_SIZE = getInstance().getInt(DefaultConfig.THREAD_POOL_CORE_POLL_SIZE_Prop);
        public static final Integer THREAD_POOL_MAX_POLL_SIZE = getInstance().getInt(DefaultConfig.THREAD_POOL_MAX_POLL_SIZE_Prop);
        public static final Long THREAD_POOL_KEEP_ALIVE_TIME = getInstance().getLong(DefaultConfig.THREAD_POOL_KEEP_ALIVE_TIME_Prop);
        public static final Integer THREAD_POOL_QUEUE_SIZE = getInstance().getInt(DefaultConfig.THREAD_POOL_QUEUE_SIZE_Prop);

        /** Http 请求配置 */
        public static final String HTTP_REQUEST_CHARSET = getInstance().getString(DefaultConfig.HTTP_REQUEST_CHARSET_Prop);
        public static final Integer HTTP_REQUEST_TIMEOUT = getInstance().getInt(DefaultConfig.HTTP_REQUEST_TIMEOUT_Prop);

        /** Redis 配置 */
        public static final String REDIS_HOST = getInstance().getString(DefaultConfig.REDIS_HOST_Prop);
        public static final Integer REDIS_PORT = getInstance().getInt(DefaultConfig.REDIS_PORT_Prop);
    }

}
