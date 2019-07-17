package com.xxg.natx.client;

import com.xxg.natx.client.handler.NatxClientHandler;
import com.xxg.natx.client.net.TcpConnection;
import com.xxg.natx.common.codec.NatxMessageDecoder;
import com.xxg.natx.common.codec.NatxMessageEncoder;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.apache.commons.cli.*;

import java.io.IOException;

/**
 * Created by wucao on 2019/2/27.
 */
public class NatxClient {

    public static void main(String[] args) throws Exception {

        // args
        Options options = new Options();
        options.addOption("h", false, "Help");
        options.addOption("server_addr", true, "Natx server address");
        options.addOption("server_port", true, "Natx server port");
        options.addOption("password", true, "Natx server password");
        options.addOption("proxy_addr", true, "Proxy server address");
        options.addOption("proxy_port", true, "Proxy server port");
        options.addOption("remote_port", true, "Proxy server remote port");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption("h")) {
            // print help
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("options", options);
        } else {

            String serverAddress = cmd.getOptionValue("server_addr");
            if (serverAddress == null) {
                System.out.println("server_addr cannot be null");
                return;
            }
            String serverPort = cmd.getOptionValue("server_port");
            if (serverPort == null) {
                System.out.println("server_port cannot be null");
                return;
            }
            String password = cmd.getOptionValue("password");
            String proxyAddress = cmd.getOptionValue("proxy_addr");
            if (proxyAddress == null) {
                System.out.println("proxy_addr cannot be null");
                return;
            }
            String proxyPort = cmd.getOptionValue("proxy_port");
            if (proxyPort == null) {
                System.out.println("proxy_port cannot be null");
                return;
            }
            String remotePort = cmd.getOptionValue("remote_port");
            if (remotePort == null) {
                System.out.println("remote_port cannot be null");
                return;
            }

            connect(serverAddress, Integer.parseInt(serverPort), password, Integer.parseInt(remotePort), proxyAddress, Integer.parseInt(proxyPort));
        }
    }

    private static void connect(String serverAddress, int serverPort, String password, int remotePort, String proxyAddress, int proxyPort) throws IOException, InterruptedException {
        TcpConnection natxConnection = new TcpConnection();
        ChannelFuture future = natxConnection.connect(serverAddress, serverPort, new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                NatxClientHandler natxClientHandler = new NatxClientHandler(remotePort, password,
                        proxyAddress, proxyPort);
                ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4),
                        new NatxMessageDecoder(), new NatxMessageEncoder(),
                        new IdleStateHandler(60, 30, 0), natxClientHandler);
            }
        });

        // channel close retry connect
        future.addListener(future1 -> new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        connect(serverAddress, serverPort, password, remotePort, proxyAddress, proxyPort);
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        }.start());
    }
}
