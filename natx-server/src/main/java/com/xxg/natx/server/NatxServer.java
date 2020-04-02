package com.xxg.natx.server;

import com.xxg.natx.common.codec.NatxMessageDecoder;
import com.xxg.natx.common.codec.NatxMessageEncoder;
import com.xxg.natx.server.handler.NatxServerHandler;
import com.xxg.natx.server.net.TcpServer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Created by wucao on 2019/2/27.
 */
public class NatxServer {

    public void start(int port, String password) throws InterruptedException {

        TcpServer natxClientServer = new TcpServer();
        natxClientServer.bind(port, new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch)
                    throws Exception {
                NatxServerHandler natxServerHandler = new NatxServerHandler(password);
                ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4),
                        new NatxMessageDecoder(), new NatxMessageEncoder(),
                        new IdleStateHandler(60, 30, 0), natxServerHandler);
            }
        });
    }
}
