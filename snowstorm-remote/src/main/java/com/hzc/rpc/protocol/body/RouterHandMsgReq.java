package com.hzc.rpc.protocol.body;

import com.hzc.rpc.common.RequestCode;
import com.hzc.rpc.protocol.header.BaseMessage;

import java.util.Set;
import java.util.UUID;

/**
 * @author: hzc
 * @Date: 2020/03/22  15:45
 * @Description:
 */
public class RouterHandMsgReq extends BaseMessage {
    private String addr;
    private String appSets;

    public RouterHandMsgReq() {
        setmCode(RequestCode.REGISTE_ROUTERS);
        setMessageId(UUID.randomUUID().toString());
    }

    public RouterHandMsgReq(String addr, Set<String> appSet) {
        setmCode(RequestCode.REGISTE_ROUTERS);
        setMessageId(UUID.randomUUID().toString());
        this.addr = addr;
        this.appSets = setToString(appSet);
    }

    public String setToString(Set<String> appSet){
        StringBuilder stringBuilder=new StringBuilder();
        appSet.forEach(s -> {
            stringBuilder.append(s).append(",");
        });
        return stringBuilder.toString();
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getAppSets() {
        return appSets;
    }

    public void setAppSets(String appSets) {
        this.appSets = appSets;
    }
}
