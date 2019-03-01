package com.xxg.natx.common.codec;

/**
 * Created by wucao on 2019/2/28.
 */
public class RegisterResultInfo {

    private boolean success;
    private String message;

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
