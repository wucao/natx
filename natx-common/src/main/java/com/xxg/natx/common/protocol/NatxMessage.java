package com.xxg.natx.common.protocol;

import java.util.Map;

/**
 * Created by wucao on 2019/3/2.
 */
public class NatxMessage {

    private NatxMessageType type;
    private Map<String, Object> metaData;
    private byte[] data;

    public NatxMessageType getType() {
        return type;
    }

    public void setType(NatxMessageType type) {
        this.type = type;
    }

    public Map<String, Object> getMetaData() {
        return metaData;
    }

    public void setMetaData(Map<String, Object> metaData) {
        this.metaData = metaData;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
