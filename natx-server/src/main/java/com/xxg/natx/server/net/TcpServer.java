package com.xxg.natx.server.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by wucao on 2019/2/27.
 */
public class TcpServer {

    public void bind(int port, final ChannelHandler ... handler) throws InterruptedException {

        final EventLoopGroup bossGroup = new NioEventLoopGroup();
        final EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch)
                            throws Exception {
                        ch.pipeline().addLast(handler);
                    }
                });
        ChannelFuture f = b.bind(port);
        f.channel().closeFuture().addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            }
        });
    }
}
