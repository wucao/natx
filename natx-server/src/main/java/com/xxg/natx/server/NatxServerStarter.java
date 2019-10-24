package com.xxg.natx.server;

import org.apache.commons.cli.*;

/**
 * Created by wucao on 2019/10/23.
 */
public class NatxServerStarter {

    public static void main(String[] args) throws ParseException, InterruptedException {

        // args
        Options options = new Options();
        options.addOption("h", false, "Help");
        options.addOption("port", true, "Natx server port");
        options.addOption("password", true, "Natx server password");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption("h")) {
            // print help
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("options", options);
        } else {

            int port = Integer.parseInt(cmd.getOptionValue("port", "7731"));
            String password = cmd.getOptionValue("password");
            NatxServer server = new NatxServer();
            server.start(port, password);

            System.out.println("Natx server started on port " + port);
        }
    }
}
