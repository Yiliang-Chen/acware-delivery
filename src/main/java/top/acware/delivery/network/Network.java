package top.acware.delivery.network;

import io.netty.channel.ChannelHandler;

/**
 * Netty 组装基类
 */
public interface Network {

    /**
     * 自定义设置 Handler
     */
    Network setHandler(ChannelHandler handler);

    /**
     * 自定义设置 ChildHandler
     */
    Network setChildHandler(ChannelHandler childHandler);

}
