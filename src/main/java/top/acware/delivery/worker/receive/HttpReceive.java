package top.acware.delivery.worker.receive;

import top.acware.delivery.callback.Callback;
import top.acware.delivery.network.NetworkServer;
import top.acware.delivery.network.handler.DefaultHttpReceiveChildHandler;
import top.acware.delivery.utils.ThreadPool;

import java.util.Map;

public class HttpReceive implements Receive {

    private final NetworkServer httpReceive;

    public HttpReceive(Callback callback, Map<?, ?> config) {
        this.httpReceive = new NetworkServer(
                (Integer) config.get("bossThreads"),
                (Integer) config.get("workerThreads"),
                (Integer) config.get("port"))
                .setChildHandler(new DefaultHttpReceiveChildHandler(
                        (Integer) config.get("max.content.length"),
                        (String) config.get("method"),
                        (String) config.get("uri"),
                        callback
                ));
    }

    @Override
    public void start() {
        ThreadPool.executor(httpReceive);
    }
}
