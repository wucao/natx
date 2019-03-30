package com.xxg.natx.client.net;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;

/**
 * A TCP connection to server
 */
public class TcpConnection {

    private Channel channel;

    /**
     *
     * @param host
     * @param port
     * @param channelInitializer
     * @throws InterruptedException
     */
    public void connect(String host, int port, ChannelInitializer channelInitializer) throws InterruptedException, IOException {

        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(channelInitializer);

            channel = b.connect(host, port).sync().channel();
            channel.closeFuture().addListener((ChannelFutureListener) future -> workerGroup.shutdownGracefully());
        } catch (Exception e) {
            workerGroup.shutdownGracefully();
            throw e;
        }
    }
}