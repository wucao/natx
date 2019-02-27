package com.xxg.natx.server.handler;

import com.xxg.natx.common.RegisterInfo;
import com.xxg.natx.server.net.TcpServer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.io.UnsupportedEncodingException;

/**
 * Created by wucao on 2019/2/27.
 */
public class NatxClientHandler extends ChannelInboundHandlerAdapter {

    private ChannelHandlerContext ctx;

    private RemoteConnectionHandler remoteConnectionHandler;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws UnsupportedEncodingException {
        this.ctx = ctx;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws InterruptedException {
        if (msg instanceof RegisterInfo) {
            RegisterInfo registerInfo = (RegisterInfo) msg;

            remoteConnectionHandler = new RemoteConnectionHandler();
            remoteConnectionHandler.setNatxClientHandler(this);

            TcpServer remoteConnectionServer = new TcpServer();
            remoteConnectionServer.bind(registerInfo.getPort(), remoteConnectionHandler);
        } else {
            remoteConnectionHandler.writeBytes((byte[]) msg);
        }
    }

    public void writeBytes(byte[] data) {
        ByteBuf out = ctx.alloc().buffer(data.length);
        out.writeBytes(data);
        ctx.writeAndFlush(out);
    }
}
