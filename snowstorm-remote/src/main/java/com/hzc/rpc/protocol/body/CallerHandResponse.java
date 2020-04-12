package com.hzc.rpc.protocol.body;

import com.hzc.rpc.protocol.header.BaseMessage;
import com.hzc.rpc.common.MessageIdCode;

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
