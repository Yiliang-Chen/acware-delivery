package top.acware.delivery.handler;

import io.netty.handler.codec.http.HttpMethod;
import org.junit.Test;
import top.acware.delivery.common.callback.DefaultCallback;
import top.acware.delivery.handler.worker.HttpReceiveWorker;

public class Handler {

    @Test
    public void http() {
        new HttpReceiveWorker.Builder()
                .bossThreads(1)
                .inetPort(9998)
                .method(HttpMethod.POST)
                .uri("/data")
                .callback(new DefaultCallback<>())
                .builder()
                .setDefaultChildHandler()
                .start();
    }

}
