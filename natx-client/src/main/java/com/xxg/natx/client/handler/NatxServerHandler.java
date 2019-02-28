package com.xxg.natx.client.handler;

import com.xxg.natx.common.handler.NatxProxyHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.json.JSONObject;

/**
 * Created by wucao on 2019/2/27.
 */
public class NatxServerHandler extends NatxProxyHandler {

    private int port;
    private String token;

    public NatxServerHandler(int port, String token) {
        this.port = port;
        this.token = token;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        // register client information
        JSONObject info = new JSONObject();
        info.put("port", port);
        info.put("token", token);
        byte[] responseByteArray = info.toString().getBytes("UTF-8");
        ByteBuf out = ctx.alloc().buffer(responseByteArray.length + 4);
        out.writeInt(responseByteArray.length);
        out.writeBytes(responseByteArray);
        ctx.writeAndFlush(out);

        super.channelActive(ctx);
    }
}
