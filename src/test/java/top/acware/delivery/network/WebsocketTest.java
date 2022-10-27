package top.acware.delivery.network;

import top.acware.delivery.common.callback.DefaultCallback;
import top.acware.delivery.common.record.KafkaRecord;
import top.acware.delivery.handler.channel.DefaultChannelHandler;
import top.acware.delivery.handler.worker.KafkaSendMessageWorker;

public class WebsocketTest {

    public static void main(String[] args) {
        WebsocketNetworkServer ws = new WebsocketNetworkServer.Builder()
                .websocketPath("/ws")
                .inetPort(9999)
                .defaultHandler(new DefaultChannelHandler(new KafkaSendMessageWorker<>(null), null))
                .build()
                .createDefaultServer();
        ws.start();
    }

}
