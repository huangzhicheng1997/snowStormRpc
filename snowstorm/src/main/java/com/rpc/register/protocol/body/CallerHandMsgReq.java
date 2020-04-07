package com.rpc.register.protocol.body;

import com.rpc.register.common.MessageIdCode;
import com.rpc.register.common.MessageType;
import com.rpc.register.common.RequestCode;
import com.rpc.register.protocol.MessageProtocol;
import com.rpc.register.protocol.header.BaseMessage;

/**
 * @author: hzc
 * @Date: 2020/03/19  15:41
 * @Description: caller握手消息
 */
public class CallerHandMsgReq extends BaseMessage {

    private String appName;

    public CallerHandMsgReq(String appName) {
        this.appName = appName;
        this.setmCode(RequestCode.CALLER_AUTH);
        //只会发送一次，所以可以固定一个msgId
        this.setMessageId(MessageIdCode.CallerHandMId);
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
