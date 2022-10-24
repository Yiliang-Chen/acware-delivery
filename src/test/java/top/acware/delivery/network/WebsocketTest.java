package top.acware.delivery.network;

import io.netty.channel.ChannelInboundHandlerAdapter;

public class WebsocketTest {

    public static void main(String[] args) {
        new WebsocketServer.Builder().websocketPath("/ws").inetPort(9999).build().createServer(new ChannelInboundHandlerAdapter() {

        });
    }

}
