package top.acware.delivery.config.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import top.acware.delivery.config.global.Constants;
import top.acware.delivery.config.global.GlobalConfig;
import top.acware.delivery.config.module.ModuleConfig;
import top.acware.delivery.exception.ConfigException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class ServerConfig {

    private static final Map<?, ?> config;

    public static final GlobalConfig global;

    public static final List<ModuleConfig> modules = new ArrayList<>();

    static {
        InputStream in = ServerConfig.class.getClassLoader().getResourceAsStream("server.json");
        try {
            config = Constants.MAPPER.readValue(in, Map.class);

            Map<?, ?> server = (Map<?, ?>) config.get("server");

            global = new GlobalConfig((Map<?, ?>) server.get("global"));

            for (String module_name : ((String) server.get("module")).split(",")) {
                log.info("Module [{}] config", module_name);
                modules.add(new ModuleConfig((Map<?, ?>) config.get(module_name)));
            }

            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            log.error(String.valueOf(e));
            throw new ConfigException("JSON file parse exception");
        }
        log.info("ServerConfig init");
    }

    public static Map<?, ?> getConfig() {
        return config;
    }

}
