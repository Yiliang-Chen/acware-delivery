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
     * 将当前连接的 channel 添加到发送列表，首次连接会启动发送线程
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.debug(" HandlerAdded invoke - {} ", ctx.channel().id().asLongText());
        sendWorker.setChannel(ctx.channel().id().asLongText(), ctx.channel());
        if (!sendWorkerStart) {
            sendWorkerStart = true;
            ThreadPool.executor(sendWorker);
        }
    }

    /**
     * 断连触发
     * 将当前连接的 channel 从队列中删除，队列中没有连接将关闭线程
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.debug(" HandlerRemoved invoke - {} ", ctx.channel().id().asLongText());
        sendWorker.channels.remove(ctx.channel().id().asLongText());
        if (sendWorker.channels.isEmpty()) {
            sendWorkerStart = false;
            sendWorker.shutdown();
        }
    }

    /**
     * 错误触发
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(" Exception occur: {}", cause.getMessage());
        handlerRemoved(ctx);
        ctx.close();
    }

}
