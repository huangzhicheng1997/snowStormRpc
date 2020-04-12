package com.hzc.rpc.protocol.body;

import com.hzc.rpc.protocol.header.BaseMessage;
import com.hzc.rpc.common.MessageIdCode;
import com.hzc.rpc.common.RequestCode;

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
