package com.hzc.rpc.protocol.body;

import com.hzc.rpc.common.RequestCode;
import com.hzc.rpc.protocol.header.BaseMessage;

/**
 * @author: hzc
 * @Date: 2020/03/22  15:48
 * @Description:
 */
public class RouterHandMsgResponse extends BaseMessage {
    private boolean isSuccess;

    public RouterHandMsgResponse() {
        setmCode(RequestCode.REGISTE_ROUTERS);
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}
