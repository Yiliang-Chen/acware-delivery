package top.acware.delivery.common.config;

import top.acware.delivery.common.exception.ConfigException;
import top.acware.delivery.utils.PropertiesTool;

import java.util.*;

/**
 * 统一从配置文件获取 并 存储配置信息
 */
public class ConfigDef {

    public final Map<String ,Object> configKeys = new HashMap<>();

    private final Properties property = new PropertiesTool().getProperties("delivery.properties");

    public ConfigDef define(String name, Type type){
        if (configKeys.containsKey(name)) {
            throw new ConfigException(" Configuration " + name + " is defined twice. ");
        }
        Object value = parseType(name, getProperty(name).trim(), type);
        configKeys.put(name, value);
        return this;
    }

    private Object parseType(String name, String value, Type type) {
        try {
            if (value == null) return null;

            switch (type) {
                case STRING:
                    return value;
                case BOOLEAN:
                    if ("true".equalsIgnoreCase(value))
                        return true;
                    else if ("false".equalsIgnoreCase(value))
                        return false;
                    else
                        throw new ConfigException(name, value, " Expected value to be either true or false ");
                case INT:
                    return Integer.parseInt(value);
                case SHORT:
                    return Short.parseShort(value);
                case LONG:
                    return Long.parseLong(value);
                case DOUBLE:
                    return Double.parseDouble(value);
                case LIST:
                    if (value.isEmpty())
                        return Collections.emptyList();
                    else
                        return Arrays.asList(value.split("\\s*,\\s*", -1));
                case CLASS:
                    return Class.forName(value,
                            true,
                            Thread.currentThread().getContextClassLoader() == null ?
                            Thread.currentThread().getContextClassLoader() : ConfigDef.class.getClassLoader());
                default:
                    throw new IllegalStateException(" Unknown type ");
            }
        } catch (ClassNotFoundException e) {
            throw new ConfigException(name, value, " Class " + value + " could not be found. ");
        } catch (NumberFormatException e) {
            throw new ConfigException(name, value, " Not a number of type " + type);
        }
    }

    private String getProperty(String name) {
        return property.getProperty(name);
    }

    public enum Type {
        BOOLEAN, STRING, INT, SHORT, LONG, DOUBLE, LIST, CLASS
    }


}
