package com.xxg.natx.client;

import com.xxg.natx.client.handler.LocalServerHandler;
import com.xxg.natx.client.handler.NatxServerHandler;
import com.xxg.natx.client.net.TcpConnection;

/**
 * Created by wucao on 2019/2/27.
 */
public class NatxClient {

    public static void main(String[] args) throws InterruptedException {

        NatxServerHandler natxServerHandler = new NatxServerHandler(10000, "qwertyui");
        LocalServerHandler localServerHandler = new LocalServerHandler();
        natxServerHandler.setLocalServerHandler(localServerHandler);
        localServerHandler.setNatxServerHandler(natxServerHandler);

        TcpConnection natxConnection = new TcpConnection();
        natxConnection.connect("localhost", 7731, natxServerHandler);

        TcpConnection localConnection = new TcpConnection();
        localConnection.connect("localhost", 8000, localServerHandler);
    }
}
