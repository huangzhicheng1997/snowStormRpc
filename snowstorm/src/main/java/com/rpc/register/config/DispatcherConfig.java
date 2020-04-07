package com.rpc.register.config;

import java.util.List;

/**
 * @author: hzc
 * @Date: 2020/03/22  16:30
 * @Description:
 */
public class DispatcherConfig extends ServerConfig {
    private List<String> routerAddrs;

    public List<String> getRouterAddrs() {
        return routerAddrs;
    }

    public void setRouterAddrs(List<String> routerAddrs) {
        this.routerAddrs = routerAddrs;
    }

    private static class Instance {
        private static DispatcherConfig dispatcherConfig = new DispatcherConfig();
    }

    public static DispatcherConfig getInstance() {
        return Instance.dispatcherConfig;
    }
}
