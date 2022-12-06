package top.acware.delivery.config.module;

import lombok.extern.slf4j.Slf4j;
import top.acware.delivery.callback.Callback;
import top.acware.delivery.callback.CallbackFactory;
import top.acware.delivery.config.warning.WarningConfig;
import top.acware.delivery.network.NetworkServer;
import top.acware.delivery.network.handler.DefaultWebsocketChildHandler;
import top.acware.delivery.worker.receive.Receive;
import top.acware.delivery.worker.receive.ReceiveFactory;
import top.acware.delivery.worker.send.Sender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class ModuleConfig {

    public final Callback callback;
    public final NetworkServer websocket;
    public final WarningConfig warningConfig;
    public final List<Receive> receives = new ArrayList<>();

    public ModuleConfig(Map<?, ?> module) {
        // warning
        warningConfig = module.containsKey("warning") ? new WarningConfig((Map<?, ?>) module.get("warning")) : null;

        // callback
        callback = CallbackFactory.factory((String) module.get("callback"),
                Long.parseLong(module.get("callback_limit").toString()),
                (Map<?, ?>) module.get(module.get("callback")));

        // Websocket
        Map<?, ?> websocketConfig = (Map<?, ?>) module.get("websocket");
        websocket = new NetworkServer(
                (Integer) websocketConfig.get("bossThreads"),
                (Integer) websocketConfig.get("workerThreads"),
                (Integer) websocketConfig.get("port"))
                .setChildHandler(new DefaultWebsocketChildHandler(
                        new Sender(callback, warningConfig == null ? null : warningConfig.warningRule),
                        (Integer) websocketConfig.get("max.content.length"),
                        (String) websocketConfig.get("uri")
                ));

        // receive
        for (String receive : ((String) module.get("receive")).split(",")) {
            receives.add(ReceiveFactory.factory(callback, (Map<?, ?>) module.get(receive)));
        }
    }

}
