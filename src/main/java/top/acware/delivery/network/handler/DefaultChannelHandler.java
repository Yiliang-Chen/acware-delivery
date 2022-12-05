package top.acware.delivery.network.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import top.acware.delivery.utils.ThreadPool;
import top.acware.delivery.worker.send.Sender;

/**
 * Websocket 默认 ChannelHandler
 * 可以继承该类重写方法
 */
@Slf4j
@ChannelHandler.Sharable
@Deprecated
public class DefaultChannelHandler extends ChannelInboundHandlerAdapter {

    public final Sender sender;
    private boolean senderStart = false;
    private final Object lock = new Object();

    public DefaultChannelHandler(Sender sender) {
        this.sender = sender;
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
        if (!senderStart) {
            synchronized (lock) {
                if (!senderStart) {
                    senderStart = true;
                    ThreadPool.executor(sender);
                }
            }
        }
    }

    // 断连触发
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.debug("Remove channel - {}", ctx.channel().id().asLongText());
        sender.channels.remove(ctx.channel().id().asLongText());
        if (senderStart && sender.channels.isEmpty()) {
            synchronized (lock) {
                if (senderStart && sender.channels.isEmpty()) {
                    senderStart = false;
                    sender.shutdown();
                }
            }
        }
    }

}
