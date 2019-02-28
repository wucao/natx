package com.xxg.natx.common.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.UnsupportedMessageTypeException;
import io.netty.util.ReferenceCountUtil;

/**
 * Created by wucao on 2019/2/28.
 */
public class NatxProxyHandler extends ChannelInboundHandlerAdapter {

    private ChannelHandlerContext ctx;

    private NatxProxyHandler natxProxyHandler;

    public void setNatxProxyHandler(NatxProxyHandler natxProxyHandler) {
        this.natxProxyHandler = natxProxyHandler;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof byte[]) {
            natxProxyHandler.writeBytes((byte[]) msg);
        } else if (msg instanceof ByteBuf) {
            ByteBuf in = (ByteBuf) msg;
            try {
                natxProxyHandler.writeBytes(ByteBufUtil.getBytes(in));
            } finally {
                ReferenceCountUtil.release(msg);
            }
        } else {
            throw new UnsupportedMessageTypeException("Unsupported message type: " + msg.getClass());
        }
    }

    public void writeBytes(byte[] data) throws Exception {
        ByteBuf out = ctx.alloc().buffer(data.length);
        out.writeBytes(data);
        ctx.writeAndFlush(out);
    }

}
