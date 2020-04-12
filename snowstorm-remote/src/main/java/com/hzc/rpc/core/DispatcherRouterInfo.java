package com.hzc.rpc.core;

import com.hzc.rpc.protocol.body.SelectNextRouterReq;
import com.hzc.rpc.util.IpUtil;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author: hzc
 * @Date: 2020/03/22  15:08
 * @Description: Router禁止部署在同一个容器中
 */
public class DispatcherRouterInfo {

    /**
     * 路由状态信息
     */
    private final ConcurrentMap<String/*router addr*/, Set<String/*appName*/>> routerInfoTable = new ConcurrentHashMap<>();

    /**
     * 负载均衡列表
     */
    private final List<String> addrList = new CopyOnWriteArrayList<>();

    /**
     * 轮询指针
     */
    private AtomicInteger loop = new AtomicInteger(0);

    private DispatcherRouterInfo() {

    }

    static class Instance {
        private static DispatcherRouterInfo dispatcherRouterInfo = new DispatcherRouterInfo();
    }

    public static DispatcherRouterInfo getInstance() {
        return Instance.dispatcherRouterInfo;
    }


    public void nodeDown(ChannelHandlerContext ctx) {
        String fromAddr = ctx.channel().remoteAddress().toString();
        String fromIp = IpUtil.getIpFromAddr(fromAddr);
        if (routerInfoTable.containsKey(fromIp)) {
            routerInfoTable.forEach((addr, appName) -> {
                String ipFromAddr = IpUtil.getIpFromAddr(addr);
                String ipFromChannel = IpUtil.getIpFromChannel(ctx.channel());
                if (ipFromAddr.equals(ipFromChannel)) {
                    routerInfoTable.remove(addr);
                    addrList.remove(addr);
                    return;
                }
            });
        }
    }

    public String getRandomNode() {
        if (CollectionUtils.isEmpty(addrList)) {
            return StringUtils.EMPTY;
        }
        int index = loop.getAndAdd(1) % addrList.size();
        return addrList.get(index);
    }

    /**
     * 获取目标路由
     *
     * @param selectNextRouterReq
     * @return
     */
    public String findTargetRouterFromRouterRequest(SelectNextRouterReq selectNextRouterReq) {
        String targetApp = selectNextRouterReq.getTargetApp();
        AtomicReference<String> targetAddr = new AtomicReference<>();
        routerInfoTable.forEach((routerAddr, appSet) -> {
            boolean flag = appSet.contains(targetApp);
            if (flag) {
                targetAddr.set(routerAddr);
            }
        });
        return targetAddr.get();
    }

    /**
     * 注册路由状态信息
     *
     * @param routerAddr
     * @param appSet
     */
    public void put(String routerAddr, Set<String> appSet) {
        if (StringUtils.isBlank(routerAddr)) {
            return;
        }
        boolean isExist = routerInfoTable.containsKey(routerAddr);
        if (isExist) {
            Set<String> oldAppNames = routerInfoTable.get(routerAddr);
            oldAppNames.addAll(appSet);
        } else {
            routerInfoTable.put(routerAddr, appSet);
        }
        addrList.add(routerAddr);

    }

    public Set<String> getAllRouterNode() {
        return routerInfoTable.keySet();
    }


}
