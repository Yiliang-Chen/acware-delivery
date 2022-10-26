package top.acware.delivery.handler.channel;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import top.acware.delivery.service.SendMessageThread;

/**
 * 默认 ChannelHandler
 */
@Slf4j
public abstract class DefaultChannelHandler extends ChannelInboundHandlerAdapter {

    public final SendMessageThread sendWorker;

    public DefaultChannelHandler(SendMessageThread sendWorker) {
        this.sendWorker = sendWorker;
    }

    /**
     * 连接触发
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.debug(" HandlerAdded invoke - {} ", ctx.channel().id().asLongText());
        sendWorker.setChannel(ctx.channel());
        super.handlerAdded(ctx);
    }

    /**
     * 断连触发
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.debug(" HandlerRemoved invoke - {} ", ctx.channel().id().asLongText());
        sendWorker.shutdown();
    }

    /**
     * 错误触发
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(" Exception occur: {}", cause.getMessage());
        sendWorker.shutdown();
        ctx.close();
    }

}
