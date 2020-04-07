package com.rpc.register.protocol.body;

import com.rpc.register.common.RequestCode;
import com.rpc.register.protocol.header.BaseMessage;

import java.util.UUID;

/**
 * @author: hzc
 * @Date: 2020/03/27  11:03
 * @Description: caller用来获取router地址的请求
 */
public class BalanceReq extends BaseMessage {
    public BalanceReq() {
        setMessageId(UUID.randomUUID().toString());
        setmCode(RequestCode.LOAD_BALANCE);
    }
}
