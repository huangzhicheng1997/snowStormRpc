package com.rpc.register.exceptions;

/**
 * @author: hzc
 * @Date: 2020/03/19  16:09
 * @Description:
 */
public class UnKnowMessageException extends RuntimeException{
    public UnKnowMessageException(String message) {
        super(message);
    }
}
