package com.hzc.rpc.exceptions;

/**
 * @author: hzc
 * @Date: 2020/03/18  21:24
 * @Description:
 */
public class SendMsgException extends RuntimeException {
    public SendMsgException(String cause) {
       super("send message Error"+cause);
    }
}
