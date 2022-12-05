package top.acware.delivery.worker.receive;

import top.acware.delivery.callback.Callback;
import top.acware.delivery.exception.ConfigException;

import java.util.Map;

public class ReceiveFactory {

    public static Receive factory(Callback callback, Map<?, ?> config) {
        switch ((String) config.get("type")) {
            case "kafka": {
                return new KafkaReceive(callback, config);
            }
            case "http": {
                return new HttpReceive(callback, config);
            }
            default: {
                throw new ConfigException(String.format("ModuleConfig parse %s receive exception", config.get("type")));
            }
        }
    }

}
