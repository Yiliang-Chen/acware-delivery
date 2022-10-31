package top.acware.delivery.handler.channel;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import top.acware.delivery.common.warning.AbstractWarning;
import top.acware.delivery.common.warning.WarnRule;
import top.acware.delivery.service.SendMessageThread;
import top.acware.delivery.utils.ThreadPool;

/**
 * 默认 ChannelHandler
 * 可以继承该类重写方法
 */
@Slf4j
@ChannelHandler.Sharable
public class DefaultChannelHandler extends ChannelInboundHandlerAdapter {

    public final SendMessageThread sendWorker;

    private boolean sendWorkerStart = false;

    public DefaultChannelHandler(SendMessageThread sendWorker) {
        this.sendWorker = sendWorker;
    }

    public DefaultChannelHandler(SendMessageThread sendWorker, WarnRule warn) {
        this.sendWorker = sendWorker;
        this.sendWorker.addWarnMethod(warn);
    }

    /**
     * 连接触发
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.debug(" HandlerAdded invoke - {} ", ctx.channel().id().asLongText());
        sendWorker.setChannel(ctx.channel().id().asLongText(), ctx.channel());
        if (!sendWorkerStart) {
            sendWorkerStart = true;
            ThreadPool.getExecutor().execute(sendWorker);
        }
    }

    /**
     * 断连触发
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.debug(" HandlerRemoved invoke - {} ", ctx.channel().id().asLongText());
        sendWorker.channels.remove(ctx.channel().id().asLongText());
        if (sendWorker.channels.isEmpty()) {
            sendWorker.shutdown();
        }
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
