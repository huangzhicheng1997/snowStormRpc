package com.hzc.rpc.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: hzc
 * @Date: 2020/03/04  14:54
 * @Description:
 */
public class GlobalLogger {
    private static final Logger logger = LoggerFactory.getLogger(Object.class);

    public static void info(String msg) {
        logger.info(msg);
    }

    public static void error(String msg) {
        logger.error(msg);
    }

}
