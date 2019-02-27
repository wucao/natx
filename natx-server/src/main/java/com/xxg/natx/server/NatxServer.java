package com.xxg.natx.server;

import com.xxg.natx.server.handler.NatxClientHandler;
import com.xxg.natx.server.handler.NatxRegisterDecoder;
import com.xxg.natx.server.net.TcpServer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * Created by wucao on 2019/2/27.
 */
public class NatxServer {

    public static void main(String[] args) throws InterruptedException {
        TcpServer natxClientServer = new TcpServer();
        natxClientServer.bind(7731, new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch)
                    throws Exception {
                ch.pipeline().addLast(new NatxRegisterDecoder(), new NatxClientHandler());
            }
        });
    }
}
