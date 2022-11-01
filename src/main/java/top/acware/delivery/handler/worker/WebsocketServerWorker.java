package top.acware.delivery.handler.worker;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;
import top.acware.delivery.common.config.GlobalConfig;
import top.acware.delivery.common.exception.NetworkException;
import top.acware.delivery.handler.channel.DefaultChannelHandler;
import top.acware.delivery.service.NettyNetwork;
import top.acware.delivery.service.WorkerThread;

/**
 * Websocket 工作线程
 */
@Slf4j
public class WebsocketServerWorker extends WorkerThread implements NettyNetwork {

    private final NioEventLoopGroup boss;
    private final NioEventLoopGroup worker;
    private final ServerBootstrap server;
    /* Websocket 服务地址 */
    private final String websocketPath;
    /* 绑定端口 */
    private final Integer inetPort;
    private final Integer maxContentLength;
    private boolean setChildHandler = true;
    /* 配置默认 handler */
    private final DefaultChannelHandler defaultHandler;
    private boolean setHandler = true;
    private boolean start = false;
    private final boolean define;

    public WebsocketServerWorker(Builder builder) {
        if (builder.bossThreads == null)
            this.boss = new NioEventLoopGroup();
        else
            this.boss = new NioEventLoopGroup(builder.bossThreads);
        if (builder.workerThreads == null)
            this.worker = new NioEventLoopGroup();
        else
            this.worker = new NioEventLoopGroup(builder.workerThreads);
        this.define = builder.define;
        this.websocketPath = builder.websocketPath;
        this.inetPort = builder.inetPort;
        this.defaultHandler = builder.defaultHandler;
        this.maxContentLength = GlobalConfig.getInstance().getInt(GlobalConfig.NETTY_MAX_CONTENT_LENGTH);
        this.server = new ServerBootstrap().group(boss, worker).channel(NioServerSocketChannel.class);
    }

    @Override
    public WebsocketServerWorker setHandler(ChannelHandler handler) {
        if (this.setHandler) {
            this.server.handler(handler);
            this.setHandler = false;
        } else {
            log.warn(" You can't invoke this method, because handler has been added ");
        }
        return this;
    }

    @Override
    public WebsocketServerWorker setChildHandler(ChannelHandler childHandler) {
        if (this.setChildHandler) {
            this.server.childHandler(childHandler);
            this.setChildHandler = false;
        } else {
            log.warn(" You can't invoke this method, because handler has been added ");
        }
        return this;
    }

    @Override
    public WebsocketServerWorker setDefaultChildHandler() {
        if (this.setChildHandler && this.define) {
            this.server.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    // 基于 http 协议
                    pipeline.addLast(new HttpServerCodec());
                    // 以块方式写
                    pipeline.addLast(new ChunkedWriteHandler());
                    // 分段大小
                    pipeline.addLast(new HttpObjectAggregator(maxContentLength));
                    // 绑定地址
                    pipeline.addLast(new WebSocketServerProtocolHandler(websocketPath));
                    // 自定义业务逻辑
                    if (defaultHandler == null)
                        throw new NetworkException(" DefaultHandler is null, invoke Builder.defaultHandler method solve ");
                    else
                        pipeline.addLast(defaultHandler);
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
    public WebsocketServerWorker setDefaultHandler() {
        if (this.setHandler && this.define) {
            this.server.handler(new LoggingHandler(LogLevel.INFO));
            setHandler = false;
        } else {
            log.warn(" You can't invoke this method, because handler has been added or other error ");
        }
        return this;
    }

    @Override
    public WebsocketServerWorker setDefinePipelineHandler(ChannelHandler... handlers) {
        if (setChildHandler) {
            this.server.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    // 基于 http 协议
                    pipeline.addLast(new HttpServerCodec());
                    // 以块方式写
                    pipeline.addLast(new ChunkedWriteHandler());
                    // 分段大小
                    pipeline.addLast(new HttpObjectAggregator(maxContentLength));
                    // 绑定地址
                    pipeline.addLast(new WebSocketServerProtocolHandler(websocketPath));
                    // 自定义
                    pipeline.addLast(handlers);
                }
            });
            setChildHandler = false;
        } else {
            log.warn(" You can't invoke this method, because handler has been added ");
        }
        return this;
    }


    @Override
    public void start() {
        if (this.start) {
            log.info(" [{}] WebsocketServer starting ", Thread.currentThread().getName());
            try {
                ChannelFuture channelFuture = this.server.bind(this.inetPort).sync();
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
        log.info(" [{}] WebsocketServer shutting down ", Thread.currentThread().getName());
        this.boss.shutdownGracefully();
        this.worker.shutdownGracefully();
        this.start = false;
    }

    public static final class Builder {
        private Integer bossThreads;
        private Integer workerThreads;
        private String websocketPath;
        private Integer inetPort;
        private DefaultChannelHandler defaultHandler;
        private boolean define;

        public Builder bossThreads(Integer nThreads) {
            this.bossThreads = nThreads;
            return this;
        }
        public Builder workerThreads(Integer nThreads) {
            this.workerThreads = nThreads;
            return this;
        }
        public Builder websocketPath(String websocketPath) {
            this.websocketPath = websocketPath;
            return this;
        }
        public Builder inetPort(Integer inetPort) {
            this.inetPort = inetPort;
            return this;
        }
        public Builder defaultHandler(DefaultChannelHandler defaultHandler) {
            this.defaultHandler = defaultHandler;
            return this;
        }

        public WebsocketServerWorker build(){
            // 如果为空，不能使用默认的方法，需要自定义实现
            if (inetPort == null || websocketPath == null || defaultHandler == null) {
                log.warn(" inetPort is null or websocketPath is null, cannot use the default method ");
                define = false;
            } else {
                define = true;
            }
            return new WebsocketServerWorker(this);
        }
    }

}
