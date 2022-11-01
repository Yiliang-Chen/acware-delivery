package top.acware.delivery.common.config;

/**
 * Callback 相关配置
 */
public class GlobalConfig extends AbstractConfig{

    private static final ConfigDef CONFIG;

    private static final GlobalConfig instance;

    /* 回调函数刷新条数 */
    public static final String CALLBACK_LIMIT = "callback.limit";
    /* Kafka 拉取数据超时时间 */
    public static final String KAFKA_POLL_TIMEOUT = "kafka.poll-timeout";
    /* Netty 接收数据最大长度 */
    public static final String NETTY_MAX_CONTENT_LENGTH = "netty.max-content-length";
    /* Email smtp 的地址 */
    public static final String EMAIL_SMTP_HOSTNAME = "email.smtp.hostname";
    public static final String EMAIL_SMTP_CHARSET = "email.smtp.charset";
    /* 发送邮件的账号 */
    public static final String EMAIL_SMTP_AUTHENTICATION_USERNAME = "email.smtp.authentication.username";
    /* 发送邮件的密码(有些是授权码) */
    public static final String EMAIL_SMTP_AUTHENTICATION_PASSWORD = "email.smtp.authentication.password";
    /* 发送邮件的账号 */
    public static final String EMAIL_SMTP_FROM_EMAIL = "email.smtp.from.email";
    /* 发送邮件的账号名称 */
    public static final String EMAIL_SMTP_FROM_NAME = "email.smtp.from.name";
    /* 线程池核心线程大小 */
    public static final String THREAD_POOL_CORE_POLL_SIZE = "thread.pool.core-pool-size";
    /* 线程池最大线程数量 */
    public static final String THREAD_POOL_MAX_POLL_SIZE = "thread.pool.max-pool-size";
    /* 线程池多余的空闲线程存活时间 */
    public static final String THREAD_POOL_KEEP_ALIVE_TIME = "thread.pool.keep-alive-time";
    /* 线程池工作队列的 Class */
    public static final String THREAD_POOL_BLOCKING_QUEUE = "thread.pool.blocking-queue";
    /* Http 请求编码 */
    public static final String HTTP_REQUEST_CHARSET = "http.request.charset";
    /* Http 请求超时时间 */
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
