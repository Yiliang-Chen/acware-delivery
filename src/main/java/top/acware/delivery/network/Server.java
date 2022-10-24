package top.acware.delivery.network;

import io.netty.channel.ChannelHandler;

public interface Server {

    /**
     * 默认创建形式，传入处理器即可
     */
    void createServer(ChannelHandler workerHandler);

    void shutdown();

    DefineNettyServer getDefineMethod();

}
