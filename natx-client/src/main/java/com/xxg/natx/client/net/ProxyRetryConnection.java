package com.xxg.natx.client.net;

import io.netty.channel.ChannelInitializer;

/**
 * Created by wucao on 2019/3/1.
 */
public class ProxyRetryConnection {

    private TcpConnection connection = new TcpConnection();

    private boolean close = false;

    public void connect(String host, int port, ChannelInitializer channelInitializer) {
        new Thread() {
            @Override
            public void run() {
                while (!close) {
                    try {
                        connection.connect(host, port, channelInitializer).sync();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println("Proxy connection break, retry connect...");
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public void close() {
        close = true;
        connection.close();
    }
}
