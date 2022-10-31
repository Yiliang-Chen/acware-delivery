package top.acware.delivery.handler.worker;

import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import top.acware.delivery.common.callback.Callback;
import top.acware.delivery.service.SendMessageThread;

/**
 * Http 接收到的消息进行推送
 */
@Slf4j
public class HttpSendMessageWorker extends SendMessageThread {

    public HttpSendMessageWorker(Callback<?> callback) {
        super(callback);
    }

    public HttpSendMessageWorker(String threadName, Callback<?> callback) {
        super(threadName, callback);
    }

    @Override
    public void work() {
        if (callback.canRead()) {
            String record = (String) callback.read();
            System.out.println(record);
            log.debug(" Send message has {} channel ", channels.size());
            for (String key : channels.keySet()) {
                channels.get(key).writeAndFlush(new TextWebSocketFrame(record));
            }
        }
    }
}
