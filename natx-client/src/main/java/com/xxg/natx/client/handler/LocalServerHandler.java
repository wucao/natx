package com.xxg.natx.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by wucao on 2019/2/27.
 */
public class LocalServerHandler extends ChannelInboundHandlerAdapter {

    private ChannelHandlerContext ctx;

    private NatxServerHandler natxServerHandler;

    public void setNatxServerHandler(NatxServerHandler natxServerHandler) {
        this.natxServerHandler = natxServerHandler;
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
            natxServerHandler.writeBytes(data);
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
