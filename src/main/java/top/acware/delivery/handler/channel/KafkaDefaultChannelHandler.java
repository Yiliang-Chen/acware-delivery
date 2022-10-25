package top.acware.delivery.handler.channel;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import top.acware.delivery.common.callback.Callback;
import top.acware.delivery.common.record.KafkaRecord;
import top.acware.delivery.handler.worker.KafkaSendMessageWorker;

@Slf4j
public class KafkaDefaultChannelHandler<K, V> extends DefaultChannelHandler{

    public KafkaDefaultChannelHandler(Callback<KafkaRecord<K, V>> callback, KafkaSendMessageWorker<K, V> worker) {
        super(callback, worker);
    }

    // 启动线程
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.debug(" HandlerRemoved invoke - {} ", ctx.channel().id().asLongText());
        worker.start();
    }

}
