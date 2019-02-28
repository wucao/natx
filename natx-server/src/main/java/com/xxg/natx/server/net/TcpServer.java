package com.xxg.natx.server.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by wucao on 2019/2/27.
 */
public class TcpServer {

    private Channel channel;

    public synchronized void bind(int port, ChannelInitializer channelInitializer) throws InterruptedException {

        final EventLoopGroup bossGroup = new NioEventLoopGroup();
        final EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(channelInitializer);
        channel = b.bind(port).sync().channel();
        channel.closeFuture().addListener((ChannelFutureListener) future -> {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        });
    }

    public synchronized void close() {
        if (channel != null) {
            channel.close();
        }
    }
}
