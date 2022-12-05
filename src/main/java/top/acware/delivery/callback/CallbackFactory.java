package top.acware.delivery.callback;

import top.acware.delivery.exception.CallbackException;

import java.util.Map;

public class CallbackFactory {

    public static Callback factory(String type, Long limit, Map<?, ?> config) {
        switch (type) {
            case "cache": {
                return new CacheCallback(limit);
            }
            case "redis":{
                return new RedisCallback(limit, config);
            }
            default: {
                throw new CallbackException(String.format("Callback type %s is not support", type));
            }
        }
    }

}
