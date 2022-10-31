package top.acware.delivery.handler.worker;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;
import top.acware.delivery.common.callback.Callback;
import top.acware.delivery.common.config.GlobalConfig;
import top.acware.delivery.common.exception.BuilderException;
import top.acware.delivery.common.exception.NetworkException;
import top.acware.delivery.common.record.StringRecord;
import top.acware.delivery.service.NettyNetwork;
import top.acware.delivery.service.WorkerThread;

import java.nio.charset.StandardCharsets;

/**
 * Http 接收数据
 */
@Slf4j
public class HttpReceiveWorker extends WorkerThread implements NettyNetwork {

    private final NioEventLoopGroup bossGroup;
    private final NioEventLoopGroup workerGroup;
    private final ServerBootstrap bootstrap;
    private final HttpMethod method;
    private final String uri;
    private final Callback<StringRecord> callback;
    private final Integer inetPort;
    private boolean setChildHandler = true;
    private boolean setHandler = true;
    private boolean start = false;
    private final boolean define;
    private final Integer maxContentLength;

    public HttpReceiveWorker(Builder builder) {
        if (builder.bossThreads == null)
            this.bossGroup = new NioEventLoopGroup();
        else
            this.bossGroup = new NioEventLoopGroup(builder.bossThreads);
        if (builder.workerThreads == null)
            this.workerGroup = new NioEventLoopGroup();
        else
            this.workerGroup = new NioEventLoopGroup(builder.workerThreads);
        this.define = builder.define;
        this.method = builder.method;
        this.uri = builder.uri;
        this.callback = builder.callback;
        this.inetPort = builder.inetPort;
        this.maxContentLength = GlobalConfig.getInstance().getInt(GlobalConfig.NETTY_MAX_CONTENT_LENGTH);
        this.bootstrap = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class);
    }

    @Override
    public HttpReceiveWorker setHandler(ChannelHandler handler) {
        if (this.setHandler) {
            this.bootstrap.handler(handler);
            setHandler = false;
        } else {
            log.warn(" You can't invoke this method, because handler has been added ");
        }
        return this;
    }

    @Override
    public HttpReceiveWorker setChildHandler(ChannelHandler childHandler) {
        if (this.setChildHandler) {
            this.bootstrap.childHandler(childHandler);
            this.setChildHandler = false;
        } else {
            log.warn(" You can't invoke this method, because handler has been added ");
        }
        return this;
    }

    @Override
    public HttpReceiveWorker setDefaultChildHandler() {
        if (this.setChildHandler && this.define) {
            this.bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
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
                                    .replace(" ", "");
                            log.debug("Headers: {}, Method: {}, URI: {}, data: {}",
                                    msg.headers(), msg.method(), msg.uri(), record);
                            if (method == msg.method() && uri.equals(msg.uri())) {
                                callback.write(new StringRecord(record));
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
            });
            this.setChildHandler = false;
            this.start = true;
        } else {
            log.warn(" You can't invoke this method, because handler has been added or other error ");
        }
        return this;
    }

    @Override
    public HttpReceiveWorker setDefaultHandler() {
        return this;
    }

    @Override
    public HttpReceiveWorker setDefinePipelineHandler(ChannelHandler... handlers) {
        if (this.setChildHandler && this.define) {
            this.bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new HttpServerCodec());
                    pipeline.addLast(new HttpObjectAggregator(maxContentLength));
                    pipeline.addLast(handlers);
                }
            });
            setChildHandler = false;
            start = true;
        } else {
            log.warn(" You can't invoke this method, because handler has been added or other error ");
        }
        return this;
    }

    @Override
    public void start() {
        if (this.start) {
            log.info(" [{}] HttpReceiverWorker starting ", Thread.currentThread().getName());
            try {
                ChannelFuture channelFuture = this.bootstrap.bind(inetPort).sync();
                channelFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                serverShutdown();
            }
        } else
            throw new NetworkException(" Not created yet. Start failed ");
    }

    @Override
    public void serverShutdown() {
        log.info(" [{}] HttpReceiverWorker shutting down ", Thread.currentThread().getName());
        this.bossGroup.shutdownGracefully();
        this.workerGroup.shutdownGracefully();
    }

    public static class Builder {
        private Integer bossThreads;
        private Integer workerThreads;
        private Integer inetPort;
        private HttpMethod method;
        private String uri;
        private Callback<StringRecord> callback;
        private boolean define;

        public Builder bossThreads(Integer nThreads) {
            this.bossThreads = nThreads;
            return this;
        }

        public Builder workerThreads(Integer nThreads) {
            this.workerThreads = nThreads;
            return this;
        }

        public Builder inetPort(Integer port) {
            this.inetPort = port;
            return this;
        }

        public Builder method(HttpMethod method) {
            this.method = method;
            return this;
        }

        public Builder uri(String uri) {
            this.uri = uri;
            return this;
        }

        public Builder callback(Callback<StringRecord> callback) {
            this.callback = callback;
            return this;
        }

        public HttpReceiveWorker builder() {
            if (this.inetPort == null || this.method == null || this.uri == null || this.callback == null) {
                define = false;
                throw new BuilderException(" Has no initialized data, cannot use the default method  ");
            } else
                define = true;
            return new HttpReceiveWorker(this);
        }
    }

}
