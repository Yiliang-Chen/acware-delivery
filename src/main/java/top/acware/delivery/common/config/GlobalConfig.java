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

    static {
        CONFIG = new ConfigDef().define(CALLBACK_LIMIT, ConfigDef.Type.INT)
                .define(KAFKA_POLL_TIMEOUT, ConfigDef.Type.LONE)
                .define(NETTY_MAX_CONTENT_LENGTH, ConfigDef.Type.INT);
        instance = new GlobalConfig();
    }

    public GlobalConfig() {
        super(CONFIG);
    }

    public static GlobalConfig getInstance() {
        return instance;
    }
}
