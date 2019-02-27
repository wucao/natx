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
public class NatxServerHandler extends ChannelInboundHandlerAdapter {

    private ChannelHandlerContext ctx;

    private int port;
    private String token;
    private LocalServerHandler localServerHandler;

    public NatxServerHandler(int port, String token) {
        this.port = port;
        this.token = token;
    }

    public void setLocalServerHandler(LocalServerHandler localServerHandler) {
        this.localServerHandler = localServerHandler;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws UnsupportedEncodingException {

        // register client information
        JSONObject info = new JSONObject();
        info.put("port", port);
        info.put("token", token);
        byte[] responseByteArray = info.toString().getBytes("UTF-8");
        ByteBuf out = ctx.alloc().buffer(responseByteArray.length + 4);
        out.writeInt(responseByteArray.length);
        out.writeBytes(responseByteArray);
        ctx.writeAndFlush(out);

        this.ctx = ctx;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        try {
            byte[] data = new byte[in.readableBytes()];
            in.readBytes(data);
            localServerHandler.writeBytes(data);
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
