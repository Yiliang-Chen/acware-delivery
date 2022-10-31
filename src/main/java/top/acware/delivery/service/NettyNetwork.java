package top.acware.delivery.service;

import io.netty.channel.ChannelHandler;

/**
 * Netty 组装基类
 */
public interface NettyNetwork {

    NettyNetwork setHandler(ChannelHandler handler);
    NettyNetwork setChildHandler(ChannelHandler childHandler);
    NettyNetwork setDefaultChildHandler();
    NettyNetwork setDefaultHandler();
    NettyNetwork setDefinePipelineHandler(ChannelHandler... handlers);

}
