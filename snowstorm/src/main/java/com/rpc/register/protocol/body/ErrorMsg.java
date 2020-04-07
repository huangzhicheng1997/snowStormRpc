package com.rpc.register.protocol.body;

import com.rpc.register.protocol.header.BaseMessage;

import java.util.UUID;

/**
 * @author: hzc
 * @Date: 2020/03/22  14:23
 * @Description:
 */
public class ErrorMsg extends BaseMessage {
    private String errorMsg;

    public ErrorMsg() {
    }

    public ErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
