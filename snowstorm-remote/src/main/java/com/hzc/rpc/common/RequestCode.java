package com.hzc.rpc.common;

/**
 * @author: hzc
 * @Date: 2020/03/17  14:05
 * @Description:
 */
public class RequestCode {
    /**
     * router获取request类型
     */
    public static final int RPC_MSG = 0;

    /**
     * caller注册请求
     */
    public static final int CALLER_AUTH = 1;

    /**
     * 向dispatcher寻找下一个路由地址
     */
    public static final int FIND_NEXT_ROUTER = 2;

    /**
     * dispatcher注册router
     */
    public static final int REGISTE_ROUTERS = 3;

    /**
     * 读取 dispatcher负载均衡处理结果
     */
    public static final int LOAD_BALANCE = 4;

}
