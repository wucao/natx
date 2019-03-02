package com.xxg.natx.client.handler;

import com.xxg.natx.client.net.TcpConnection;
import com.xxg.natx.common.exception.NatxException;
import com.xxg.natx.common.handler.NatxCommonHandler;
import com.xxg.natx.common.protocol.NatxMessage;
import com.xxg.natx.common.protocol.NatxMessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wucao on 2019/2/27.
 */
public class NatxClientHandler extends NatxCommonHandler {

    private int port;
    private String password;
    private String proxyAddress;
    private int proxyPort;

    private ConcurrentHashMap<String, NatxCommonHandler> channelHandlerMap = new ConcurrentHashMap<>();
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public NatxClientHandler(int port, String password, String proxyAddress, int proxyPort) {
        this.port = port;
        this.password = password;
        this.proxyAddress = proxyAddress;
        this.proxyPort = proxyPort;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        // register client information
        NatxMessage natxMessage = new NatxMessage();
        natxMessage.setType(NatxMessageType.REGISTER);
        HashMap<String, Object> metaData = new HashMap<>();
        metaData.put("port", port);
        metaData.put("password", password);
        natxMessage.setMetaData(metaData);
        ctx.writeAndFlush(natxMessage);

        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        NatxMessage natxMessage = (NatxMessage) msg;
        if (natxMessage.getType() == NatxMessageType.REGISTER_RESULT) {
            processRegisterResult(natxMessage);
        } else if (natxMessage.getType() == NatxMessageType.CONNECTED) {
            processConnected(natxMessage);
        } else if (natxMessage.getType() == NatxMessageType.DISCONNECTED) {
            processDisconnected(natxMessage);
        } else if (natxMessage.getType() == NatxMessageType.DATA) {
            processData(natxMessage);
        } else {
            throw new NatxException("Unknown type: " + natxMessage.getType());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        channelGroup.close();
        System.out.println("Loss connection to Natx server, Please restart!");
    }

    /**
     * if natxMessage.getType() == NatxMessageType.REGISTER_RESULT
     */
    private void processRegisterResult(NatxMessage natxMessage) {
        if ((Boolean) natxMessage.getMetaData().get("success")) {
            System.out.println("Register to Natx server");
        } else {
            System.out.println("Register fail: " + natxMessage.getMetaData().get("reason"));
            ctx.close();
        }
    }

    /**
     * if natxMessage.getType() == NatxMessageType.CONNECTED
     */
    private void processConnected(NatxMessage natxMessage) throws Exception {

        try {
            NatxClientHandler thisHandler = this;
            TcpConnection localConnection = new TcpConnection();
            localConnection.connect(proxyAddress, proxyPort, new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    LocalProxyHandler localProxyHandler = new LocalProxyHandler(thisHandler, natxMessage.getMetaData().get("channelId").toString());
                    ch.pipeline().addLast(new ByteArrayDecoder(), new ByteArrayEncoder(), localProxyHandler);

                    channelHandlerMap.put(natxMessage.getMetaData().get("channelId").toString(), localProxyHandler);
                    channelGroup.add(ch);
                }
            });
        } catch (Exception e) {
            NatxMessage message = new NatxMessage();
            message.setType(NatxMessageType.DISCONNECTED);
            HashMap<String, Object> metaData = new HashMap<>();
            metaData.put("channelId", natxMessage.getMetaData().get("channelId"));
            message.setMetaData(metaData);
            ctx.writeAndFlush(message);
            channelHandlerMap.remove(natxMessage.getMetaData().get("channelId"));
            throw e;
        }
    }

    /**
     * if natxMessage.getType() == NatxMessageType.DISCONNECTED
     */
    private void processDisconnected(NatxMessage natxMessage) {
        String channelId = natxMessage.getMetaData().get("channelId").toString();
        NatxCommonHandler handler = channelHandlerMap.get(channelId);
        if (handler != null) {
            handler.getCtx().close();
            channelHandlerMap.remove(channelId);
        }
    }

    /**
     * if natxMessage.getType() == NatxMessageType.DATA
     */
    private void processData(NatxMessage natxMessage) {
        String channelId = natxMessage.getMetaData().get("channelId").toString();
        NatxCommonHandler handler = channelHandlerMap.get(channelId);
        if (handler != null) {
            ChannelHandlerContext ctx = handler.getCtx();
            ctx.writeAndFlush(natxMessage.getData());
        }
    }
}
