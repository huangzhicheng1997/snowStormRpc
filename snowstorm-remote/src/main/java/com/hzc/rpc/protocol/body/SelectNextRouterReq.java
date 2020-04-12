package com.hzc.rpc.protocol.body;

import com.hzc.rpc.common.RequestCode;
import com.hzc.rpc.protocol.header.BaseMessage;

import java.util.UUID;

/**
 * @author: hzc
 * @Date: 2020/03/22  14:09
 * @Description:
 */
public class SelectNextRouterReq extends BaseMessage {
    /**
     * 目标服务名
     */
    private String targetApp;

    public SelectNextRouterReq() {
        setmCode(RequestCode.FIND_NEXT_ROUTER);
        setMessageId(UUID.randomUUID().toString());
    }

    public String getTargetApp() {
        return targetApp;
    }

    public void setTargetApp(String targetApp) {
        this.targetApp = targetApp;
    }
}
