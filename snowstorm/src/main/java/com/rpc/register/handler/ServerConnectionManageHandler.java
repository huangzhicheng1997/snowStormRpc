package com.rpc.register.handler;

import io.netty.channel.*;
import io.netty.handler.timeout.IdleStateEvent;


/**
 * @author: hzc
 * @Date: 2020/03/03  15:18
 * @Description: 连接管理
 */
@ChannelHandler.Sharable
public class ServerConnectionManageHandler extends ChannelInboundHandlerAdapter {
    /**
     * channel关闭策略
     */
    private CloseChannelStrategy closeChannelStrategy;

    public ServerConnectionManageHandler(CloseChannelStrategy closeChannelStrategy) {
        this.closeChannelStrategy = closeChannelStrategy;
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        closeChannelStrategy.closeChannelGraceFully(ctx);
    }
}
