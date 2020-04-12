package com.hzc.rpc.core.event;

import com.hzc.rpc.protocol.body.RpcReq;
import io.netty.channel.Channel;

/**
 * @author: hzc
 * @Date: 2020/03/30  11:39
 * @Description:
 */
public interface CallerHandleEvent {

    /**
     * 线程事件处理完毕 回调方法
     *
     * @param rpcResult
     */
    void eventNotify(Object rpcResult);

    /**
     * 获取channel
     *
     * @return
     */
    Channel getChannel();

    /**
     * 获取Rpc调用信息
     *
     * @return
     */
    RpcReq getRpcReq();
}
