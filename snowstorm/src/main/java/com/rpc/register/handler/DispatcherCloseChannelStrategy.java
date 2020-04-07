package com.rpc.register.handler;

import com.rpc.register.core.DispatcherRouterInfo;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author: hzc
 * @Date: 2020/03/26  16:50
 * @Description:
 */
public class DispatcherCloseChannelStrategy implements CloseChannelStrategy {
    /**
     * 优雅关闭Channel
     *
     * @param context
     */
    @Override
    public void closeChannelGraceFully(ChannelHandlerContext context) {
        DispatcherRouterInfo.getInstance().nodeDown(context);
    }
}
