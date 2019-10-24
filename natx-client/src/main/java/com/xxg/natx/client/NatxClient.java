package com.xxg.natx.client;

import com.xxg.natx.client.handler.NatxClientHandler;
import com.xxg.natx.client.net.TcpConnection;
import com.xxg.natx.common.codec.NatxMessageDecoder;
import com.xxg.natx.common.codec.NatxMessageEncoder;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.io.IOException;

/**
 * Created by wucao on 2019/2/27.
 */
public class NatxClient {

    public void connect(String serverAddress, int serverPort, String password, int remotePort, String proxyAddress, int proxyPort) throws IOException, InterruptedException {
        TcpConnection natxConnection = new TcpConnection();
        ChannelFuture future = natxConnection.connect(serverAddress, serverPort, new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                NatxClientHandler natxClientHandler = new NatxClientHandler(remotePort, password,
                        proxyAddress, proxyPort);
                ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4),
                        new NatxMessageDecoder(), new NatxMessageEncoder(),
                        new IdleStateHandler(60, 30, 0), natxClientHandler);
            }
        });

        // channel close retry connect
        future.addListener(future1 -> new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        connect(serverAddress, serverPort, password, remotePort, proxyAddress, proxyPort);
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        }.start());
    }
}
