package com.hzc.rpc.protocol.body;

import com.hzc.rpc.protocol.header.BaseMessage;

/**
 * @author: hzc
 * @Date: 2020/03/27  16:29
 * @Description:
 */
public class BalanceResponse extends BaseMessage {
    private String routerAddr;

    public BalanceResponse() {
    }

    public String getRouterAddr() {
        return routerAddr;
    }

    public void setRouterAddr(String routerAddr) {
        this.routerAddr = routerAddr;
    }
}
