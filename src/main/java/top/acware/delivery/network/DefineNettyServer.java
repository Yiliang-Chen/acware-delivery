package top.acware.delivery.network;

import io.netty.channel.ChannelHandler;

public class DefineNettyServer {

    private WebsocketServer websocketServer;

    public DefineNettyServer (WebsocketServer websocketServer) {
        this.websocketServer = websocketServer;
    }

    public DefineNettyServer defineNettyHandler(ChannelHandler handler) {
        websocketServer.server.handler(handler);
        return this;
    }

    public DefineNettyServer defineNettyChildHandler(ChannelHandler childHandler) {
        websocketServer.server.childHandler(childHandler);
        return this;
    }

}
