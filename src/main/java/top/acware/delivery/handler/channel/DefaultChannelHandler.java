package top.acware.delivery.handler.channel;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import top.acware.delivery.common.callback.Callback;
import top.acware.delivery.common.record.Record;
import top.acware.delivery.service.SendMessageThread;

/**
 * 默认 ChannelHandler
 */
@Slf4j
public abstract class DefaultChannelHandler extends ChannelInboundHandlerAdapter {

    public final Callback<? extends Record> callback;

    public final SendMessageThread worker;

    public DefaultChannelHandler(Callback<? extends Record> callback, SendMessageThread worker) {
        this.callback = callback;
        this.worker = worker;
    }

    /**
     * 连接触发
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.debug(" HandlerAdded invoke - {} ", ctx.channel().id().asLongText());
        super.handlerAdded(ctx);
    }

    /**
     * 断连触发
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.debug(" HandlerRemoved invoke - {} ", ctx.channel().id().asLongText());
        worker.shutdown();
    }

    /**
     * 错误触发
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(" Exception occur: {}", cause.getMessage());
        worker.shutdown();
        ctx.close();
    }

}
