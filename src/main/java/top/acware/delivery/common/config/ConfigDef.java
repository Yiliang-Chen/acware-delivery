package top.acware.delivery.common.config;

import top.acware.delivery.common.exception.ConfigException;
import top.acware.delivery.utils.PropertiesTool;

import java.util.*;

/**
 * 统一从配置文件获取 并 存储配置信息
 * 注意：必须存在 delivery.properties 配置
 */
public class ConfigDef {

    public static final Object NO_DEFAULT_VALUE = "";

    public final Map<String ,Object> configKeys = new HashMap<>();

    private final Properties property = new PropertiesTool().getProperties("delivery.properties");

    public ConfigDef define(String name, Type type){
        return define(name, type, NO_DEFAULT_VALUE);
    }

    public ConfigDef define(String name, Type type, Object defaultValue) {
        if (configKeys.containsKey(name)) {
            throw new ConfigException(" Configuration " + name + " is defined twice. ");
        }
        Object userProp = getProperty(name);
        Object value;
        if (userProp == null || userProp.equals(NO_DEFAULT_VALUE)) {
            value = defaultValue == NO_DEFAULT_VALUE ? NO_DEFAULT_VALUE : parseType(name, defaultValue, type);
        } else {
            value = parseType(name, userProp, type);
        }
        configKeys.put(name, value);
        return this;
    }

    private Object parseType(String name, Object value, Type type) {
        try {
            if (value == null) return null;

            String trimmed = null;
            if (value instanceof String)
                trimmed = ((String) value).trim();

            switch (type) {
                case BOOLEAN:
                    if (value instanceof String) {
                        if (trimmed.equalsIgnoreCase("true"))
                            return true;
                        else if (trimmed.equalsIgnoreCase("false"))
                            return false;
                        else
                            throw new ConfigException(name, value, "Expected value to be either true or false");
                    } else if (value instanceof Boolean)
                        return value;
                    else
                        throw new ConfigException(name, value, "Expected value to be either true or false");
                case STRING:
                    if (value instanceof String)
                        return trimmed;
                    else
                        throw new ConfigException(name, value, "Expected value to be a string, but it was a " + value.getClass().getName());
                case INT:
                    if (value instanceof Integer) {
                        return (Integer) value;
                    } else if (value instanceof String) {
                        return Integer.parseInt(trimmed);
                    } else {
                        throw new ConfigException(name, value, "Expected value to be an number.");
                    }
                case SHORT:
                    if (value instanceof Short) {
                        return (Short) value;
                    } else if (value instanceof String) {
                        return Short.parseShort(trimmed);
                    } else {
                        throw new ConfigException(name, value, "Expected value to be an number.");
                    }
                case LONG:
                    if (value instanceof Integer)
                        return ((Integer) value).longValue();
                    if (value instanceof Long)
                        return (Long) value;
                    else if (value instanceof String)
                        return Long.parseLong(trimmed);
                    else
                        throw new ConfigException(name, value, "Expected value to be an number.");
                case DOUBLE:
                    if (value instanceof Number)
                        return ((Number) value).doubleValue();
                    else if (value instanceof String)
                        return Double.parseDouble(trimmed);
                    else
                        throw new ConfigException(name, value, "Expected value to be an number.");
                case LIST:
                    if (value instanceof List)
                        return (List<?>) value;
                    else if (value instanceof String)
                        if (trimmed.isEmpty())
                            return Collections.emptyList();
                        else
                            return Arrays.asList(trimmed.split("\\s*,\\s*", -1));
                    else
                        throw new ConfigException(name, value, "Expected a comma separated list.");
                case CLASS:
                    if (value instanceof Class)
                        return (Class<?>) value;
                    else if (value instanceof String)
                        return Class.forName(trimmed, true, this.getClass().getClassLoader());
                    else
                        throw new ConfigException(name, value, "Expected a Class instance or class name.");
                default:
                    throw new IllegalStateException("Unknown type.");
            }
        } catch (NumberFormatException e) {
            throw new ConfigException(name, value, "Not a number of type " + type);
        } catch (ClassNotFoundException e) {
            throw new ConfigException(name, value, "Class " + value + " could not be found.");
        }
    }

    private String getProperty(String name) {
        if (property == null)
            return null;
        return property.getProperty(name);
    }

    public enum Type {
        BOOLEAN, STRING, INT, SHORT, LONG, DOUBLE, LIST, CLASS
    }


}
