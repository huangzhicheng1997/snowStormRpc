package com.hzc.rpc.common;

/**
 * @author: hzc
 * @Date: 2020/03/04  13:34
 * @Description: 消息类型
 */
public enum MessageType {
    /**
     * 业务消息request
     */
    BUSSINESS_MSG_REQ(1),

    /**
     * 业务消息response
     */
    BUSSINESS_MSG_RES(2),
    ;


    MessageType(Integer type) {
        this.type = type;
    }

    private Integer type;

    public Integer getType() {
        return type;
    }
}
