package top.acware.delivery.network.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;
import top.acware.delivery.callback.Callback;
import top.acware.delivery.record.ObjectRecord;

import java.nio.charset.StandardCharsets;

@Slf4j
public class DefaultHttpReceiveChildHandler extends ChannelInitializer<SocketChannel> {

    private final Integer maxContentLength;
    private final String method;
    private final String uri;
    private final Callback callback;

    public DefaultHttpReceiveChildHandler(Integer maxContentLength, String method, String uri, Callback callback) {
        this.maxContentLength = maxContentLength;
        this.method = method;
        this.uri = uri;
        this.callback = callback;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(maxContentLength));
        pipeline.addLast(new SimpleChannelInboundHandler<FullHttpRequest>() {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
                String record = msg.content().toString(StandardCharsets.UTF_8)
                        .replace("\n", "")
                        .replace("\t", "")
                        .replace("\r", "")
                        .replace(" ", "");
                if (method.equals(msg.method().name()) && uri.equals(msg.uri())) {
                    // 将接收到到数据写入回调函数
                    log.debug("Record: {}", record);
                    callback.write(new ObjectRecord(record));
                    ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK));
                    ctx.close();
                } else {
                    log.warn(" Method or URI mismatching, discarding the data ");
                    ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.METHOD_NOT_ALLOWED));
                    ctx.close();
                }
            }
        });
    }
}
