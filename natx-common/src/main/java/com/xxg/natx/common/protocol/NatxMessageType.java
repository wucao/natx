package com.xxg.natx.common.protocol;

import java.util.NoSuchElementException;

/**
 * Created by wucao on 2019/3/2.
 */
public enum NatxMessageType {

    REGISTER(1),
    REGISTER_RESULT(2),
    CONNECTED(3),
    DISCONNECTED(4),
    DATA(5);

    private int code;

    NatxMessageType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static NatxMessageType valueOf(int code) {
        for (NatxMessageType item : NatxMessageType.values()) {
            if (item.code == code) {
                return item;
            }
        }
        throw new NoSuchElementException("NatxMessageType code error: " + code);
    }
}
