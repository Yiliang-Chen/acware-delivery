package top.acware.delivery.network;

import io.netty.channel.ChannelHandler;

public interface NetworkServer {

    /**
     * 默认创建形式，传入处理器即可
     *
     * @return
     */
    WebsocketNetworkServer createDefaultServer(ChannelHandler... workerHandler);

    void serverShutdown();

    void start();

    WebsocketNetworkServer.DefineNettyServer getDefineMethod();

}
