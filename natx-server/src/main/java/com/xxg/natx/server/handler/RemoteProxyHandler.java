package com.xxg.natx.server.handler;

import com.xxg.natx.common.handler.NatxCommonHandler;
import com.xxg.natx.common.protocol.NatxMessage;
import com.xxg.natx.common.protocol.NatxMessageType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;

/**
 * Created by wucao on 2019/3/2.
 */
public class RemoteProxyHandler extends NatxCommonHandler {

    private NatxCommonHandler proxyHandler;

    public RemoteProxyHandler(NatxCommonHandler proxyHandler) {
        this.proxyHandler = proxyHandler;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        NatxMessage message = new NatxMessage();
        message.setType(NatxMessageType.CONNECTED);
        HashMap<String, Object> metaData = new HashMap<>();
        metaData.put("channelId", ctx.channel().id().asLongText());
        message.setMetaData(metaData);
        proxyHandler.getCtx().writeAndFlush(message);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        NatxMessage message = new NatxMessage();
        message.setType(NatxMessageType.DISCONNECTED);
        HashMap<String, Object> metaData = new HashMap<>();
        metaData.put("channelId", ctx.channel().id().asLongText());
        message.setMetaData(metaData);
        proxyHandler.getCtx().writeAndFlush(message);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        byte[] data = ByteBufUtil.getBytes((ByteBuf) msg);
        NatxMessage message = new NatxMessage();
        message.setType(NatxMessageType.DATA);
        message.setData(data);
        HashMap<String, Object> metaData = new HashMap<>();
        metaData.put("channelId", ctx.channel().id().asLongText());
        message.setMetaData(metaData);
        proxyHandler.getCtx().writeAndFlush(message);
    }
}
