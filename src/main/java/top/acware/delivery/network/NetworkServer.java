package top.acware.delivery.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * 网络服务线程
 */
@Slf4j
public class NetworkServer extends NetworkThread {

    // boss 线程组
    private final NioEventLoopGroup boss;
    // worker 线程组
    private final NioEventLoopGroup worker;
    // Netty 服务
    private final ServerBootstrap server;
    // 服务端口
    private final Integer port;

    public NetworkServer(Integer bossThreads, Integer workerThreads, Integer port) {
        if (bossThreads == null || bossThreads == 0)
            this.boss = new NioEventLoopGroup();
        else
            this.boss = new NioEventLoopGroup(bossThreads);
        if (workerThreads == null || workerThreads == 0)
            this.worker = new NioEventLoopGroup();
        else
            this.worker = new NioEventLoopGroup(workerThreads);
        this.port = port;
        this.server = new ServerBootstrap().group(boss, worker).channel(NioServerSocketChannel.class);
    }

    @Override
    public NetworkServer setHandler(ChannelHandler handler) {
        this.server.handler(handler);
        return this;
    }

    @Override
    public NetworkServer setChildHandler(ChannelHandler childHandler) {
        this.server.childHandler(childHandler);
        return this;
    }

    @Override
    public void start() {
        log.info("[{}] NetworkServer starting ...", this.port);
        try {
            ChannelFuture channelFuture = this.server.bind(this.port).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            serverShutdown();
        }
    }

    @Override
    public void serverShutdown() {
        log.info("[{}] NetworkServer shutting down", this.port);
        this.boss.shutdownGracefully();
        this.worker.shutdownGracefully();
    }
}
