package com.xxg.natx.server.handler;

import com.xxg.natx.common.codec.MessageInfo;
import com.xxg.natx.common.codec.RegisterInfo;
import com.xxg.natx.common.handler.NatxProxyHandler;
import com.xxg.natx.server.net.TcpServer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.json.JSONObject;

/**
 * Created by wucao on 2019/2/27.
 */
public class NatxClientHandler extends NatxProxyHandler {

    private TcpServer remoteConnectionServer = new TcpServer();

    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private RegisterInfo registerInfo;

    private String token;

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof RegisterInfo) {
            RegisterInfo registerInfo = (RegisterInfo) msg;

            JSONObject info = new JSONObject();

            if (token != null && !token.equals(registerInfo.getToken())) {
                info.put("success", false);
                info.put("message", "Token is wrong");
            } else {

                try {

                    NatxClientHandler thisNatxClientHandler = this;
                    remoteConnectionServer.bind(registerInfo.getPort(), new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch)
                                throws Exception {
                            channels.add(ch);
                            NatxProxyHandler remoteConnectionHandler = new NatxClientHandler();
                            remoteConnectionHandler.setNatxProxyHandler(thisNatxClientHandler);
                            setNatxProxyHandler(remoteConnectionHandler);
                            ch.pipeline().addLast(remoteConnectionHandler);
                        }
                    });

                    info.put("success", true);

                    this.registerInfo = registerInfo;
                    System.out.println("Start server on port: " + registerInfo.getPort());
                } catch (Exception e) {
                    info.put("success", false);
                    info.put("message", e.getMessage());
                    e.printStackTrace();
                }
            }

            byte[] responseByteArray = info.toString().getBytes("UTF-8");
            ByteBuf out = ctx.alloc().buffer(responseByteArray.length + 4);
            out.writeInt(responseByteArray.length);
            out.writeBytes(responseByteArray);
            ctx.writeAndFlush(out);
            if (!info.getBoolean("success")) {
                System.out.println("Client register error: " + info.getString("message"));
                ctx.close();
            }
        } else {
            MessageInfo messageInfo = (MessageInfo) msg;
            channels.writeAndFlush(messageInfo.getData(), channel -> channel.id().asLongText().equals(messageInfo.getChannelId()));
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        remoteConnectionServer.close();
        if (registerInfo != null) {
            System.out.println("Stop server on port: " + registerInfo.getPort());
        }
    }
}
