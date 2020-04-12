package com.hzc.rpc.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @author: hzc
 * @Date: 2020/03/02  18:20
 * @Description:
 */
public class SystemUtil {
    /**
     * 判断系统类型
     *
     * @return
     */
    public static Boolean isLinux() {
        String osName = System.getProperties().getProperty("os.name");
        if (StringUtils.isNotBlank(osName) && osName.toLowerCase().equals("linux")) {
            return true;
        }
        return false;
    }

}
