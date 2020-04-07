package com.rpc.register.exceptions;

/**
 * @author: hzc
 * @Date: 2020/03/04  10:49
 * @Description:
 */
public class LengthFieldTooLongException extends RuntimeException {

    public LengthFieldTooLongException(String message) {
        super(message);
    }
}
