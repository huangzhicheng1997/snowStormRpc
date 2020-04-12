package com.hzc.rpc.util;

import io.netty.channel.Channel;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;

/**
 * @author: hzc
 * @Date: 2020/01/19  11:39
 * @Description:
 */
public class IpUtil {
    public static String getIpFromChannel(Channel channel) {
        String s = channel.remoteAddress().toString().split(":")[0];
        String substring = s.substring(1);
        return substring;
    }

    public static String getIpFromAddr(String addr) {
        return addr.split(":")[0];
    }

    public static SocketAddress string2SocketAddress(final String addr) {
        int split = addr.lastIndexOf(":");
        String host = addr.substring(0, split);
        String port = addr.substring(split + 1);
        InetSocketAddress isa = new InetSocketAddress(host, Integer.parseInt(port));
        return isa;
    }

    public static String getLocalIp() {
        InetAddress localHost = null;
        try {
            localHost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        assert localHost != null;
        return localHost.getHostAddress();
    }


}
