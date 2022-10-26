package top.acware.delivery.network;

import io.netty.channel.ChannelInboundHandlerAdapter;
import top.acware.delivery.common.callback.DefaultCallback;
import top.acware.delivery.handler.channel.KafkaDefaultChannelHandler;
import top.acware.delivery.handler.worker.KafkaSendMessageWorker;

public class WebsocketTest {

    public static void main(String[] args) {
        WebsocketNetworkServer ws = new WebsocketNetworkServer.Builder()
                .websocketPath("/ws")
                .inetPort(9999)
                .defaultHandler(new KafkaDefaultChannelHandler<>(new KafkaSendMessageWorker<>(new DefaultCallback<>())))
                .build()
                .createDefaultServer();
        ws.start();
    }

}
