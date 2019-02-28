package com.xxg.natx.server.handler;

import com.xxg.natx.common.RegisterInfo;
import com.xxg.natx.common.handler.NatxProxyHandler;
import com.xxg.natx.server.net.TcpServer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * Created by wucao on 2019/2/27.
 */
public class NatxClientHandler extends NatxProxyHandler {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof RegisterInfo) {
            RegisterInfo registerInfo = (RegisterInfo) msg;

            final NatxClientHandler thisNatxClientHandler = this;
            TcpServer remoteConnectionServer = new TcpServer();
            remoteConnectionServer.bind(registerInfo.getPort(), new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch)
                        throws Exception {
                    NatxProxyHandler remoteConnectionHandler = new NatxClientHandler();
                    remoteConnectionHandler.setNatxProxyHandler(thisNatxClientHandler);
                    setNatxProxyHandler(remoteConnectionHandler);
                    ch.pipeline().addLast(remoteConnectionHandler);
                }
            });
        } else {
            super.channelRead(ctx, msg);
        }
    }

}
