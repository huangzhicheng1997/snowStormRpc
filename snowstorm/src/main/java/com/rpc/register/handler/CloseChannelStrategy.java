package com.rpc.register.handler;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author: hzc
 * @Date: 2020/03/26  11:50
 * @Description:
 */
public interface CloseChannelStrategy {
    /**
     * 优雅关闭Channel
     *
     * @param context
     */
    void closeChannelGraceFully(ChannelHandlerContext context);
}
