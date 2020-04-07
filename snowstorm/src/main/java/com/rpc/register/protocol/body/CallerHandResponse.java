package com.rpc.register.protocol.body;

import com.rpc.register.common.MessageIdCode;
import com.rpc.register.protocol.header.BaseMessage;

/**
 * @author: hzc
 * @Date: 2020/03/20  16:39
 * @Description: (返回值不需要设置mcode)
 */
public class CallerHandResponse extends BaseMessage {
    private boolean isSuccess;

    public CallerHandResponse() {
    }

    public CallerHandResponse(boolean isSuccess) {
        setMessageId(MessageIdCode.CallerHandMId);
        this.isSuccess = isSuccess;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}
