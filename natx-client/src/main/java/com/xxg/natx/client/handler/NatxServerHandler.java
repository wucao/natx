package com.xxg.natx.client.handler;

import com.xxg.natx.client.net.TcpConnection;
import com.xxg.natx.common.codec.RegisterResultInfo;
import com.xxg.natx.common.handler.NatxProxyHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import org.json.JSONObject;

/**
 * Created by wucao on 2019/2/27.
 */
public class NatxServerHandler extends NatxProxyHandler {

    private int port;
    private String token;
    private String proxyAddress;
    private int proxyPort;

    private TcpConnection localConnection = new TcpConnection();;

    public NatxServerHandler(int port, String token, String proxyAddress, int proxyPort) {
        this.port = port;
        this.token = token;
        this.proxyAddress = proxyAddress;
        this.proxyPort = proxyPort;
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

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof RegisterResultInfo) {
            RegisterResultInfo registerResultInfo = (RegisterResultInfo) msg;

            if (!registerResultInfo.getSuccess()) {
                System.out.println("Register fail: " + registerResultInfo.getMessage());
                ctx.close();
            } else {
                System.out.println("Register to Natx server");

                NatxServerHandler thisHandler = this;
                localConnection.connect(proxyAddress, proxyPort, new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        NatxProxyHandler localServerHandler = new NatxProxyHandler();
                        localServerHandler.setNatxProxyHandler(thisHandler);
                        setNatxProxyHandler(localServerHandler);
                        ch.pipeline().addLast(localServerHandler);
                    }
                });
            }
        } else {
            super.channelRead(ctx, msg);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        localConnection.close();
        System.out.println("Loss connection to Natx server, Please restart!");
    }
}
