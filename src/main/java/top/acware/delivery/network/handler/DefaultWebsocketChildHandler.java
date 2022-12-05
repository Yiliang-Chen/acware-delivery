package top.acware.delivery.network.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;
import top.acware.delivery.utils.ThreadPool;
import top.acware.delivery.worker.send.Sender;

@Slf4j
public class DefaultWebsocketChildHandler extends ChannelInitializer<SocketChannel> {

    public final Sender sender;
    private final Integer maxContentLength;
    private final String websocketPath;

    public DefaultWebsocketChildHandler(Sender sender, Integer maxContentLength, String websocketPath) {
        log.info("[{}] Websocket config", websocketPath);
        this.sender = sender;
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
        // 业务逻辑
        pipeline.addLast(new DefaultWebsocketHandler(sender));
    }

    @Sharable
    static class DefaultWebsocketHandler extends ChannelInboundHandlerAdapter {

        public final Sender sender;
        private final Object lock;

        public DefaultWebsocketHandler(Sender sender) {
            this.sender = sender;
            this.lock = new Object();
        }

        // 错误触发
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            log.error("Exception: {}", cause.getMessage());
            handlerRemoved(ctx);
            ctx.close();
        }

        // 连接触发
        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
            log.debug("Add channel - {}", ctx.channel().id().asLongText());
            sender.setChannel(ctx.channel().id().asLongText(), ctx.channel());
            if (!sender.start) {
                synchronized (lock) {
                    if (!sender.start) {
                        ThreadPool.executor(sender);
                        sender.start = true;
                    }
                }
            }
        }

        // 断连触发
        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
            log.debug("Remove channel - {}", ctx.channel().id().asLongText());
            sender.channels.remove(ctx.channel().id().asLongText());
            if (sender.start && sender.channels.isEmpty()) {
                synchronized (lock) {
                    if (sender.start && sender.channels.isEmpty()) {
                        sender.start = false;
                        sender.shutdown();
                    }
                }
            }
        }
    }

}
