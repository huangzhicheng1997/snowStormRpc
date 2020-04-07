package com.rpc.register.exceptions;

/**
 * @author: hzc
 * @Date: 2020/03/03  15:15
 * @Description:
 */
public class EmptyMsgException extends RuntimeException {

    public EmptyMsgException() {
        super("msg must not be null or empty!");
    }
}
