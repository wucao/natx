package com.xxg.natx.server.handler;

import com.xxg.natx.common.handler.NatxCommonHandler;
import com.xxg.natx.common.protocol.NatxMessage;
import com.xxg.natx.common.protocol.NatxMessageType;
import com.xxg.natx.server.net.TcpServer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.HashMap;

/**
 * Created by wucao on 2019/2/27.
 */
public class NatxServerHandler extends NatxCommonHandler {

    private TcpServer remoteConnectionServer = new TcpServer();

    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private String password;
    private int port;

    private boolean register = false;

    public NatxServerHandler(String password) {
        this.password = password;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        NatxMessage natxMessage = (NatxMessage) msg;
        if (natxMessage.getType() == NatxMessageType.REGISTER) {
            processRegister(natxMessage);
        } else if (register) {
            if (natxMessage.getType() == NatxMessageType.DISCONNECTED) {
                processDisconnected(natxMessage);
            } else if (natxMessage.getType() == NatxMessageType.DATA) {
                processData(natxMessage);
            }
        } else {
            ctx.close();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        remoteConnectionServer.close();
        if (register) {
            System.out.println("Stop server on port: " + port);
        }
    }

    /**
     * if natxMessage.getType() == NatxMessageType.REGISTER
     */
    private void processRegister(NatxMessage natxMessage) {
        HashMap<String, Object> metaData = new HashMap<>();

        String password = natxMessage.getMetaData().get("password").toString();
        if (this.password != null && !this.password.equals(password)) {
            metaData.put("success", false);
            metaData.put("reason", "Token is wrong");
        } else {
            int port = (int) natxMessage.getMetaData().get("port");

            try {

                NatxServerHandler thisHandler = this;
                remoteConnectionServer.bind(port, new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ByteArrayDecoder(), new ByteArrayEncoder(), new RemoteProxyHandler(thisHandler));
                        channels.add(ch);
                    }
                });

                metaData.put("success", true);
                this.port = port;
                register = true;
                System.out.println("Register success, start server on port: " + port);
            } catch (Exception e) {
                metaData.put("success", false);
                metaData.put("reason", e.getMessage());
                e.printStackTrace();
            }
        }

        NatxMessage sendBackMessage = new NatxMessage();
        sendBackMessage.setType(NatxMessageType.REGISTER_RESULT);
        sendBackMessage.setMetaData(metaData);
        ctx.writeAndFlush(sendBackMessage);

        if (!register) {
            System.out.println("Client register error: " + metaData.get("reason"));
            ctx.close();
        }
    }

    /**
     * if natxMessage.getType() == NatxMessageType.DATA
     */
    private void processData(NatxMessage natxMessage) {
        channels.writeAndFlush(natxMessage.getData(), channel -> channel.id().asLongText().equals(natxMessage.getMetaData().get("channelId")));
    }

    /**
     * if natxMessage.getType() == NatxMessageType.DISCONNECTED
     * @param natxMessage
     */
    private void processDisconnected(NatxMessage natxMessage) {
        channels.close(channel -> channel.id().asLongText().equals(natxMessage.getMetaData().get("channelId")));
    }
}
