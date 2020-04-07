package com.rpc.register.protocol.body;

import com.rpc.register.common.RequestCode;
import com.rpc.register.protocol.header.BaseMessage;

/**
 * @author: hzc
 * @Date: 2020/03/22  14:12
 * @Description:
 */
public class SelectNextRouterResponse extends BaseMessage {
    private String nextRouterIp;

    public SelectNextRouterResponse() {
    }

    public String getNextRouterIp() {
        return nextRouterIp;
    }

    public void setNextRouterIp(String nextRouterIp) {
        this.nextRouterIp = nextRouterIp;
    }
}
