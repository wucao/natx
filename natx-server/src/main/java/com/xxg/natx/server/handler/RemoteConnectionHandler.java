package com.xxg.natx.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.io.UnsupportedEncodingException;

/**
 * Created by wucao on 2019/2/27.
 */
public class RemoteConnectionHandler extends ChannelInboundHandlerAdapter {

    private ChannelHandlerContext ctx;

    private NatxClientHandler natxClientHandler;

    public void setNatxClientHandler(NatxClientHandler natxClientHandler) {
        this.natxClientHandler = natxClientHandler;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws UnsupportedEncodingException {
        this.ctx = ctx;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        try {
            byte[] data = new byte[in.readableBytes()];
            in.readBytes(data);
            natxClientHandler.writeBytes(data);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    public void writeBytes(byte[] data) {
        ByteBuf out = ctx.alloc().buffer(data.length);
        out.writeBytes(data);
        ctx.writeAndFlush(out);
    }
}
