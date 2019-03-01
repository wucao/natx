package com.xxg.natx.server;

import com.xxg.natx.server.handler.NatxClientHandler;
import com.xxg.natx.common.codec.NatxRegisterDecoder;
import com.xxg.natx.server.net.TcpServer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import org.apache.commons.cli.*;

/**
 * Created by wucao on 2019/2/27.
 */
public class NatxServer {

    public static void main(String[] args) throws InterruptedException, ParseException {

        // args
        Options options = new Options();
        options.addOption("h", false, "Help");
        options.addOption("p", true, "Natx server port");
        options.addOption("t", true, "Natx server token");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption("h")) {
            // print help
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("options", options);
        } else {

            int port = Integer.parseInt(cmd.getOptionValue("p", "7731"));
            String token = cmd.getOptionValue("t");

            TcpServer natxClientServer = new TcpServer();
            natxClientServer.bind(port, new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch)
                        throws Exception {
                    NatxClientHandler natxClientHandler = new NatxClientHandler();
                    natxClientHandler.setToken(token);
                    ch.pipeline().addLast(new NatxRegisterDecoder(), natxClientHandler);
                }
            });
            System.out.println("Natx server started on port " + port);
        }
    }
}
