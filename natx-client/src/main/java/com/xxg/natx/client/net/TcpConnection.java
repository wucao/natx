package com.xxg.natx.client.net;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;

/**
 * Created by wucao on 2019/2/27.
 */
public class TcpConnection {

    private Channel channel;

    private boolean close = false;

    /**
     *
     * @param host
     * @param port
     * @param channelInitializer
     * @return channel close future
     * @throws InterruptedException
     */
    public synchronized ChannelFuture connect(String host, int port, ChannelInitializer channelInitializer) throws InterruptedException, IOException {

        if (close) {
            throw new IOException("Tcp connection closed");
        }
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.handler(channelInitializer);

            channel = b.connect(host, port).sync().channel();
            return channel.closeFuture().addListener((ChannelFutureListener) future -> workerGroup.shutdownGracefully());
        } catch (Exception e) {
            workerGroup.shutdownGracefully();
            throw e;
        }
    }

    public synchronized void close() {
        if (channel != null) {
            channel.close();
        }
        close = true;
    }
}