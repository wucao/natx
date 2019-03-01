package com.xxg.natx.common.codec;

/**
 * Created by wucao on 2019/3/2.
 */
public class MessageInfo {

    private String channelId;

    private byte[] data;

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
