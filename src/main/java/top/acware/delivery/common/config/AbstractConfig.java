package top.acware.delivery.common.config;

import lombok.extern.slf4j.Slf4j;
import top.acware.delivery.common.exception.ConfigException;

import java.util.List;

/**
 * 统一获取方式
 */
@Slf4j
public class AbstractConfig {

    private final ConfigDef config;

    public AbstractConfig(ConfigDef configDef) {
        this.config = configDef;
    }

    public Object get(String key) {
        if (!config.configKeys.containsKey(key)) {
            throw new ConfigException(String.format(" Unknown configuration '%s' ", key));
        }
        return config.configKeys.get(key);
    }

    public Short getShort(String key) {
        return (Short) get(key);
    }

    public Integer getInt(String key) {
        return (Integer) get(key);
    }

    public Long getLong(String key) {
        return (Long) get(key);
    }

    public Double getDouble(String key) {
        return (Double) get(key);
    }

    @SuppressWarnings("unchecked")
    public List<String> getList(String key) {
        return (List<String>) get(key);
    }

    public Boolean getBoolean(String key) {
        return (Boolean) get(key);
    }

    public String getString(String key) {
        return (String) get(key);
    }

    public Class<?> getClass(String key) {
        return (Class<?>) get(key);
    }

}
