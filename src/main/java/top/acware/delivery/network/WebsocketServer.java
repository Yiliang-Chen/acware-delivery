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
import top.acware.delivery.common.NetworkException;
import top.acware.delivery.common.config.GlobalConfig;

@Slf4j
public class WebsocketServer implements Server{

    private final NioEventLoopGroup boss;
    private final NioEventLoopGroup worker;
    public final ServerBootstrap server;
    private final String websocketPath;
    private final Integer inetPort;
    private final Integer maxContentLength;
    private boolean canDefine = true;

    public WebsocketServer(Builder builder) {
        if (builder.bossThreads == null)
            this.boss = new NioEventLoopGroup();
        else
            this.boss = new NioEventLoopGroup(builder.bossThreads);
        if (builder.workerThreads == null)
            this.worker = new NioEventLoopGroup();
        else
            this.worker = new NioEventLoopGroup(builder.workerThreads);
        this.server = new ServerBootstrap();
        this.websocketPath = builder.websocketPath;
        this.inetPort = builder.inetPort;
        this.maxContentLength = GlobalConfig.getInstance().getInt(GlobalConfig.NETTY_MAX_CONTENT_LENGTH);
    }

    @Override
    public void createServer(ChannelHandler workerHandler) {
        canDefine = false;
        try {
            this.server.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
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
                            pipeline.addLast(workerHandler);
                        }
                    });
            ChannelFuture channelFuture = server.bind(inetPort).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            shutdown();
        }
    }

    @Override
    public DefineNettyServer getDefineMethod() {
        if (canDefine) {
            return new DefineNettyServer(this);
        } else {
            log.error(" You can't get define method, createServer method has been called ");
            throw new NetworkException(" You can't get define method, createServer method has been called ");
        }
    }

    @Override
    public void shutdown() {
        log.info(" [{}] WebsocketServer shutting down ", Thread.currentThread().getName());
        this.boss.shutdownGracefully();
        this.worker.shutdownGracefully();
    }

    public static final class Builder {
        private Integer bossThreads;
        private Integer workerThreads;
        private String websocketPath;
        private Integer inetPort;

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

        public WebsocketServer build(){
            return new WebsocketServer(this);
        }
    }
}
