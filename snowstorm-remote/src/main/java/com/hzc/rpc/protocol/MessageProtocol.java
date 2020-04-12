package com.hzc.rpc.protocol;

import com.hzc.rpc.protocol.body.ErrorMsg;
import com.hzc.rpc.protocol.header.BaseMessage;
import com.hzc.rpc.common.MessageType;
import lombok.Data;

/**
 * @author: hzc
 * @Date: 2020/03/03  13:42
 * @Description: 消息协议
 *
 * <p>
 * _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _  _ _ _ _ _ _ _
 * | type  |  size  |   classNameLength |    className  |      msg              |
 * |   4   |   4    |          4        |classNameLength|size-4-classNameLength |
 * |_ _ _ _| _ _ _ _| _ _ _ _ _ _ _ _ _ |_ _ _ _ _ _ _ _| _ _ _ _ _ __ _ _ _ _ _|
 * <p>
 * type：表示消息类型                 用int表示    消耗4个字节
 * size：表示消息body长度             用int表示    消耗4个字节
 * classNameLength+className+msg:   消息body    消耗size个字节
 * <p>
 * body:
 * classNameLength: 表示序列化对象类名长度  用int表示  消耗4个字节
 * className:       表示类名              消耗classNameLength个字节
 * </p>
 */
@Data
public class MessageProtocol {
    /**
     * 1.业务request 2.业务response 3.握手认证
     */
    private int type;

    /**
     * 消息内容
     */
    private BaseMessage content;

    public MessageProtocol(int type) {
        this.type = type;

    }

    public MessageProtocol(int type, BaseMessage content) {
        this.type = type;
        this.content = content;
    }

    /**
     * 获取空body 消息实体
     *
     * @param messageType
     * @return
     */
    public static MessageProtocol getNoBodyMessage(int messageType) {
        return new MessageProtocol(messageType);
    }

    public static MessageProtocol createMessage(MessageType messageType, BaseMessage baseMessage) {
        return new MessageProtocol(messageType.getType(), baseMessage);
    }

    public static MessageProtocol createRequest(MessageType messageType, BaseMessage baseMessage) {
        return new MessageProtocol(messageType.getType(), baseMessage);
    }

    public static MessageProtocol errorMsg(String errorInfo,String oldMessageId){
        ErrorMsg errorMsg = new ErrorMsg(errorInfo);
        errorMsg.setMessageId(oldMessageId);
        return new MessageProtocol(MessageType.BUSSINESS_MSG_RES.getType());
    }
}
