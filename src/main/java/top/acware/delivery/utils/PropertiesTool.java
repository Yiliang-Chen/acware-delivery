package top.acware.delivery.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * 获取配置文件
 */
public class PropertiesTool {

    public Properties getProperties(String fileName) {
        Properties properties = new Properties();
        try {
            InputStream in = this.getClass().getClassLoader().getResourceAsStream(fileName);
            if (in != null)
                properties.load(new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }

}
