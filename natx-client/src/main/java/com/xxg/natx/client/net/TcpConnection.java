package com.xxg.natx.client.net;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by wucao on 2019/2/27.
 */
public class TcpConnection {

    public void connect(String host, int port, final ChannelHandler handler) throws InterruptedException {

        final EventLoopGroup workerGroup = new NioEventLoopGroup();

        Bootstrap b = new Bootstrap();
        b.group(workerGroup);
        b.channel(NioSocketChannel.class);
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(handler);
            }
        });
        ChannelFuture f = b.connect(host, port).sync();
        f.channel().closeFuture().addListener((ChannelFutureListener) future -> workerGroup.shutdownGracefully());
    }
}