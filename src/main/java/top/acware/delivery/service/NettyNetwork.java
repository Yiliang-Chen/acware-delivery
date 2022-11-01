package top.acware.delivery.service;

import io.netty.channel.ChannelHandler;

/**
 * Netty 组装基类
 */
public interface NettyNetwork {

    /**
     * 自定义设置 handler
     */
    NettyNetwork setHandler(ChannelHandler handler);

    /**
     * 自定义设置 ChildHandler
     */
    NettyNetwork setChildHandler(ChannelHandler childHandler);

    /**
     * 默认配置 ChildHandler
     */
    NettyNetwork setDefaultChildHandler();

    /**
     * 设置默认 Handler
     */
    NettyNetwork setDefaultHandler();

    /**
     * 自定义 pipeline 中的 自定义部分 handler
     */
    NettyNetwork setDefinePipelineHandler(ChannelHandler... handlers);

}
