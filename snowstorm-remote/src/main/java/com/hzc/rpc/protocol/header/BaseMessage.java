package com.hzc.rpc.protocol.header;

/**
 * @author: hzc
 * @Date: 2020/03/17  14:04
 * @Description:
 */
public  class BaseMessage {
    /**
     * 消息编号
     */
    private int mCode;

    /**
     * 请求id或响应id
     */
    private String messageId;



    public int getmCode() {
        return mCode;
    }

    public void setmCode(int mCode) {
        this.mCode = mCode;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

}
