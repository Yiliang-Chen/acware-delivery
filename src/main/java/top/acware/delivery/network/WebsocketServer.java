package top.acware.delivery.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import top.acware.delivery.exception.DeliveryException;
import top.acware.delivery.network.handler.DefaultChildHandler;

/**
 * Websocket 服务网络线程
 */
@Slf4j
@Deprecated
public class WebsocketServer extends NetworkThread {

    // boss 线程组
    private final NioEventLoopGroup boss;
    // worker 线程组
    private final NioEventLoopGroup worker;
    // Netty 服务
    private final ServerBootstrap server;
    // Websocket 接口地址
    private final String websocketPath;
    // Websocket 端口
    private final Integer inetPort;
    // Websocket 最大分段大小
    private final Integer maxContentLength;
    // 默认配置 handler
    private final ChannelHandler[] pipelineHandler;
    // 是否是自定义模式
    private final boolean define;
    // 是否可以设置 ChildHandler
    private boolean setChildHandler = true;
    // 是否可以设置 Handler
    private boolean setHandler = true;
    // 是否可以启动
    private boolean canStart = false;

    public WebsocketServer(Builder builder) {
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
        this.pipelineHandler = builder.pipelineHandler;
        this.maxContentLength = builder.maxContentLength;
        this.server = new ServerBootstrap().group(boss, worker).channel(NioServerSocketChannel.class);
    }

    public static final class Builder {
        private Integer bossThreads;
        private Integer workerThreads;
        private String websocketPath;
        private Integer inetPort;
        private Integer maxContentLength;
        private ChannelHandler[] pipelineHandler;
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

        public Builder maxContentLength(Integer maxContentLength) {
            this.maxContentLength = maxContentLength;
            return this;
        }

        public Builder pipelineHandler(ChannelHandler... pipelineHandler) {
            this.pipelineHandler = pipelineHandler;
            return this;
        }

        public WebsocketServer build() {
            // 如果为空，不能使用默认的方法，需要自定义实现
            if (inetPort == null || websocketPath == null) {
                log.info("inetPort is null or websocketPath is null, cannot use the default method.");
                define = true;
            } else {
                define = false;
            }
            return new WebsocketServer(this);
        }
    }

    @Override
    public WebsocketServer setHandler(ChannelHandler handler) {
        if (this.setHandler) {
            this.server.handler(handler);
            this.setHandler = false;
        } else {
            log.warn("You can't invoke this method, because handler has been added");
        }
        return this;
    }

    @Override
    public WebsocketServer setChildHandler(ChannelHandler childHandler) {
        if (this.setChildHandler) {
            this.server.childHandler(childHandler);
            this.setChildHandler = false;
            this.canStart = true;
        } else {
            log.warn("You can't invoke this method, because handler has been added");
        }
        return this;
    }

    public WebsocketServer setDefaultHandler() {
        if (this.setHandler && !this.define) {
            this.server.handler(new LoggingHandler(LogLevel.INFO));
            this.setHandler = false;
        } else {
            log.warn("You can't invoke this method, because handler has been added");
        }
        return this;
    }

    public WebsocketServer setDefaultChildHandler() {
        if (this.setChildHandler && !this.define) {
            this.server.childHandler(new DefaultChildHandler(pipelineHandler, maxContentLength, websocketPath));
            this.setChildHandler = false;
            this.canStart = true;
        } else {
            log.warn("You can't invoke this method, because handler has been added");
        }
        return this;
    }

    @Override
    public void start() {
        if (this.canStart) {
            log.info("[{}] WebsocketServer starting ...", websocketPath);
            try {
                ChannelFuture channelFuture = this.server.bind(this.inetPort).sync();
                channelFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                serverShutdown();
            }
        } else {
            throw new DeliveryException("[WebsocketServer] Not created yet. Start failed");
        }
    }

    @Override
    public void serverShutdown() {
        log.info("[{}] WebsocketServer shutting down", websocketPath);
        this.boss.shutdownGracefully();
        this.worker.shutdownGracefully();
        this.canStart = false;
    }
}
