package com.xxg.natx.client;

import com.xxg.natx.client.handler.NatxClientHandler;
import com.xxg.natx.client.net.TcpConnection;
import com.xxg.natx.common.codec.NatxMessageDecoder;
import com.xxg.natx.common.codec.NatxMessageEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.apache.commons.cli.*;

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

            TcpConnection natxConnection = new TcpConnection();
            natxConnection.connect(serverAddress, Integer.parseInt(serverPort), new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    NatxClientHandler natxClientHandler = new NatxClientHandler(Integer.parseInt(remotePort), password,
                            proxyAddress, Integer.parseInt(proxyPort));
                    ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4),
                            new NatxMessageDecoder(), new NatxMessageEncoder(), natxClientHandler);
                }
            });
        }
    }
}
