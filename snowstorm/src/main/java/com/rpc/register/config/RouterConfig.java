package com.rpc.register.config;

/**
 * @author: hzc
 * @Date: 2020/03/22  14:06
 * @Description:
 */
public class RouterConfig extends ServerConfig {
    public String dispatcherAddr;

    public String getDispatcherAddr() {
        return dispatcherAddr;
    }

    public void setDispatcherAddr(String dispatcherAddr) {
        this.dispatcherAddr = dispatcherAddr;
    }

    private static class Instance {
        private static RouterConfig routerConfig = new RouterConfig();
    }

    public static RouterConfig getInstance() {
        return Instance.routerConfig;
    }
}
