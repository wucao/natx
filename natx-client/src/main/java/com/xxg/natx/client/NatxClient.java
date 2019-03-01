package com.xxg.natx.client;

import com.xxg.natx.client.handler.NatxServerHandler;
import com.xxg.natx.client.net.TcpConnection;
import com.xxg.natx.common.codec.NatxRegisterResultDecoder;
import com.xxg.natx.common.handler.NatxProxyHandler;

/**
 * Created by wucao on 2019/2/27.
 */
public class NatxClient {

    public static void main(String[] args) throws InterruptedException {

        NatxServerHandler natxServerHandler = new NatxServerHandler(10001, "123456");
        NatxProxyHandler localServerHandler = new NatxProxyHandler();
        natxServerHandler.setNatxProxyHandler(localServerHandler);
        localServerHandler.setNatxProxyHandler(natxServerHandler);

        TcpConnection natxConnection = new TcpConnection();
        natxConnection.connect("localhost", 7732, new NatxRegisterResultDecoder(), natxServerHandler);

        TcpConnection localConnection = new TcpConnection();
        localConnection.connect("localhost", 8000, localServerHandler);
    }
}
