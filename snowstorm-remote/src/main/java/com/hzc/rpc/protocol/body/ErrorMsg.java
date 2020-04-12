package com.hzc.rpc.protocol.body;

import com.hzc.rpc.protocol.header.BaseMessage;

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
