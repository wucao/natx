package com.xxg.natx.common.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by wucao on 2019/2/28.
 */
public class NatxCommonHandler extends ChannelInboundHandlerAdapter {

    protected ChannelHandlerContext ctx;

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("Exception caught ...");
        cause.printStackTrace();
    }
}
