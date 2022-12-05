package top.acware.delivery.network.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import top.acware.delivery.exception.DeliveryException;

@Deprecated
public class DefaultChildHandler extends ChannelInitializer<SocketChannel> {

    private final ChannelHandler[] pipelineHandler;
    private final Integer maxContentLength;
    private final String websocketPath;

    public DefaultChildHandler(ChannelHandler[] pipelineHandler, Integer maxContentLength, String websocketPath) {
        this.pipelineHandler = pipelineHandler;
        this.maxContentLength = maxContentLength;
        this.websocketPath = websocketPath;
    }

    @Override
    protected void initChannel(SocketChannel ch){
        ChannelPipeline pipeline = ch.pipeline();
        // 基于 HTTP 协议
        pipeline.addLast(new HttpServerCodec());
        // 以块方式写
        pipeline.addLast(new ChunkedWriteHandler());
        // 分段大小
        if (maxContentLength != null)
            pipeline.addLast(new HttpObjectAggregator(maxContentLength));
        // 绑定地址
        pipeline.addLast(new WebSocketServerProtocolHandler(websocketPath));
        // 自定义业务逻辑
        if (pipelineHandler != null) {
            for (ChannelHandler channelHandler : pipelineHandler) {
                pipeline.addLast(channelHandler);
            }
        } else {
            throw new DeliveryException("Pipeline handler no define");
        }
    }
}
