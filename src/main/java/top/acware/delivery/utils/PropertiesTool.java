package top.acware.delivery.utils;

import java.io.IOException;
import java.util.Properties;

/**
 * 获取配置文件
 */
public class PropertiesTool {

    public Properties getProperties(String fileName) {
        Properties properties = new Properties();
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream(fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }

}
