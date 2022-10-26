package top.acware.delivery.network;

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
import top.acware.delivery.common.exception.NetworkException;
import top.acware.delivery.common.config.GlobalConfig;
import top.acware.delivery.handler.channel.DefaultChannelHandler;

/**
 * 构建 Netty Websocket Server
 * 自定义需要全部自定义，包括 handler 处理方式
 */
@Slf4j
public class WebsocketNetworkServer implements NetworkServer {

    private final NioEventLoopGroup boss;
    private final NioEventLoopGroup worker;
    private final ServerBootstrap server;
    private final String websocketPath;
    private final Integer inetPort;
    private final Integer maxContentLength;
    private final DefaultChannelHandler defaultHandler;
    private boolean canStart = false;
    private boolean canDefine = true;

    public WebsocketNetworkServer(Builder builder) {
        if (builder.bossThreads == null)
            this.boss = new NioEventLoopGroup();
        else
            this.boss = new NioEventLoopGroup(builder.bossThreads);
        if (builder.workerThreads == null)
            this.worker = new NioEventLoopGroup();
        else
            this.worker = new NioEventLoopGroup(builder.workerThreads);
        this.server = new ServerBootstrap().group(boss, worker).channel(NioServerSocketChannel.class);
        this.websocketPath = builder.websocketPath;
        this.inetPort = builder.inetPort;
        this.defaultHandler = builder.defaultHandler;
        this.maxContentLength = GlobalConfig.getInstance().getInt(GlobalConfig.NETTY_MAX_CONTENT_LENGTH);
    }

    /**
     * 创建默认配置的服务
     */
    @Override
    public WebsocketNetworkServer createDefaultServer(ChannelHandler... handlers) {
        return getDefineMethod(this).defineNettyHandler(new LoggingHandler(LogLevel.INFO))
                .defineNettyChildHandler(new ChannelInitializer<SocketChannel>() {
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
                        if (handlers.length != 0) {
                            log.debug(" Add define handlers");
                            pipeline.addLast(handlers);
                        } else {
                            if (defaultHandler == null) {
                                throw new NetworkException(" DefaultHandler is null, invoke defaultHandler method solve ");
                            } else {
                                pipeline.addLast(defaultHandler);
                                log.debug(" Add defaultHandler ");
                            }
                        }
                    }
                }).create();
    }

    /**
     * 获取自定义配置方法
     */
    @Override
    public DefineNettyServer getDefineMethod(WebsocketNetworkServer websocketNetworkServer) {
        if (canDefine) {
            return new DefineNettyServer(websocketNetworkServer);
        }
        log.error(" You can't get define method, createDefaultServer method has been invoked ");
        throw new NetworkException(" You can't get define method, createServer method has been invoked ");
    }

    /**
     * 启动服务
     */
    @Override
    public void start() {
        if (canStart) {
            log.info(" [{}] WebsocketServer starting ", Thread.currentThread().getName());
            try {
                ChannelFuture channelFuture = this.server.bind(inetPort).sync();
                channelFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                serverShutdown();
            }
        } else
            throw new NetworkException(" Not created yet. Start failed ");
    }

    /**
     * 停止服务
     */
    @Override
    public void serverShutdown() {
        log.info(" [{}] WebsocketServer shutting down ", Thread.currentThread().getName());
        this.boss.shutdownGracefully();
        this.worker.shutdownGracefully();
    }

    /**
     * 自定义配置
     */
    public static final class DefineNettyServer {
        private final WebsocketNetworkServer websocketServer;

        public DefineNettyServer (WebsocketNetworkServer websocketServer) {
            this.websocketServer = websocketServer;
        }

        /**
         * 自定义 handler
         */
        public DefineNettyServer defineNettyHandler(ChannelHandler handler) {
            websocketServer.server.handler(handler);
            return this;
        }

        /**
         * 自定义 ChildHandler
         * @return
         */
        public DefineNettyServer defineNettyChildHandler(ChannelHandler childHandler) {
            websocketServer.server.childHandler(childHandler);
            return this;
        }

        /**
         * 创建
         */
        public WebsocketNetworkServer create() {
            websocketServer.canStart = true;
            websocketServer.canDefine = false;
            return websocketServer;
        }
    }

    public static final class Builder {
        private Integer bossThreads;
        private Integer workerThreads;
        private String websocketPath;
        private Integer inetPort;
        private DefaultChannelHandler defaultHandler;

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

        public WebsocketNetworkServer build(){
            if (inetPort == null)
                throw new NetworkException(" InetPort is null, invoke inetPort method solve ");
            if (websocketPath == null)
                throw new NetworkException(" WebsocketPath is null, invoke websocketPath method solve ");
            return new WebsocketNetworkServer(this);
        }
    }
}
