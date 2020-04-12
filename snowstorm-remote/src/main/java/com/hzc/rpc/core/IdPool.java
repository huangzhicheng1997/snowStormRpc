package com.hzc.rpc.core;

import com.hzc.rpc.util.IpUtil;
import io.netty.channel.Channel;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: hzc
 * @Date: 2020/01/07  17:51
 * @Description: id分配池
 */
@Deprecated
public class IdPool {
    /**
     * 私有锁
     */
    private final Object mutex = new Object();

    /**
     * id池
     */
    private ConcurrentHashMap<Long, ConcurrentHashMap<String, Long>> poolRegister = null;

    /**
     * 注册开关
     */
    private volatile boolean allowRegister = true;

    /**
     * 数据中心容量
     */
    private static final int DATA_CENTER_CAP = 32;

    /**
     * 节点容量
     */
    private static final int WORKER_CAP = 32;


    private IdPool() {
        poolRegister = new ConcurrentHashMap<>(DATA_CENTER_CAP);
        for (long i = 0; i < DATA_CENTER_CAP; i++) {
            poolRegister.put(i, new ConcurrentHashMap<>(WORKER_CAP));
        }
    }

    /**
     * 实例
     */
    private static class Instance {
        public static IdPool idPool = new IdPool();
    }

    public static IdPool getInstance() {
        return Instance.idPool;
    }

    /**
     * 获取客户端注册信息
     *
     * @param channel
     * @return
     */
    public Map<Long, Long> getDataIdAndWorkerId(Channel channel) {
        String ip = IpUtil.getIpFromChannel(channel);
        for (long i = 0; i < DATA_CENTER_CAP; i++) {
            ConcurrentHashMap<String, Long> workerMap = poolRegister.get(i);
            Long val = workerMap.get(ip);
            if (null != val) {
                Map<Long, Long> map = new HashMap<>();
                map.put(i, val);
                return map;
            }
        }
        return null;
    }


    /**
     * 注册客户端
     *
     * @param channel
     */
    public void registerClient(Channel channel) {
        if (!allowRegister) {
            for (; ; ) {
                if (allowRegister) {
                    break;
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


        try {
            String node = channel.remoteAddress().toString();
            Boolean exist = isExist(node);
            if (exist) {
                return;
            }
            synchronized (mutex) {
                //分配注册id
                ConcurrentHashMap<String, Long> minAvaliableCenter = getMinAvaliableCenter();
                if (null != minAvaliableCenter) {
                    for (long i = 0; i < WORKER_CAP; i++) {
                        boolean contains = minAvaliableCenter.contains(i);
                        if (!contains) {
                            minAvaliableCenter.put(node, i);
                            return;
                        }
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 注销客户端
     *
     * @param channel
     */
    public void removeClient(Channel channel) {
        String ip = IpUtil.getIpFromChannel(channel);
        synchronized (mutex) {
            for (long i = 0; i < DATA_CENTER_CAP; i++) {
                ConcurrentHashMap<String, Long> workerMap = poolRegister.get(i);
                Long val = workerMap.get(ip);
                if (null != val) {
                    workerMap.remove(ip);
                    System.out.println("disconnect from " + ip);
                    return;
                }
            }
        }
    }


    /**
     * 拒绝注册
     */
    public void rejectRegister() {
        allowRegister = false;
    }

    /**
     * 开启注册
     *
     * @return
     */
    public void allowRegister() {
        allowRegister = true;
    }

    /**
     * 获取注册开关
     *
     * @return
     */
    public boolean getRegisterAccess() {
        return allowRegister;
    }

    /**
     * 获取可用的DataCenter
     *
     * @return
     */
    private ConcurrentHashMap<String, Long> getMinAvaliableCenter() {
        for (long i = 0; i < DATA_CENTER_CAP; i++) {
            ConcurrentHashMap<String, Long> workerMap = poolRegister.get(i);
            if (workerMap.size() < WORKER_CAP) {
                return workerMap;
            }
        }
        return null;

    }

    /*
     * 非线程安全，在宕机后 服务检测出上次关闭或重启时还有部分连接，会恢复上次的注册信息
     * 并在恢复前阻塞其他客户端注册，根据具体配置的拒绝时间,会阻塞一段时间客户端的注册行为
     * 在此时间内会采用该方法恢复宕机前的注册信息。在使用时，采取的是串行推送任务到线程池大小为1的固定
     * 线程池中所以也是线程安全的。其他情况不可以使用此方法
     */

    /**
     * 恢复所有注册信息
     */
    public void recoverRegistInfo(Long dataId, Long workerId, String ip) {
        ConcurrentHashMap<String, Long> concurrentHashMap = new ConcurrentHashMap<>();
        concurrentHashMap.put(ip, workerId);
        poolRegister.put(dataId, concurrentHashMap);
    }

    /**
     * 是否已经注册
     *
     * @param node
     * @return
     */
    private Boolean isExist(String node) {
        boolean flag = false;
        Set<Map.Entry<Long, ConcurrentHashMap<String, Long>>> entries = poolRegister.entrySet();
        for (Map.Entry<Long, ConcurrentHashMap<String, Long>> entry : entries) {
            ConcurrentHashMap<String, Long> workMap = entry.getValue();
            flag = flag || workMap.containsKey(node);
        }
        return flag;
    }

    public Integer getWorkerSize() {
        int counter = 0;
        Set<Map.Entry<Long, ConcurrentHashMap<String, Long>>> entries = poolRegister.entrySet();
        for (Map.Entry<Long, ConcurrentHashMap<String, Long>> entry : entries) {
            ConcurrentHashMap<String, Long> workMap = entry.getValue();
            counter = counter + workMap.size();
        }
        return counter;
    }

    /**
     * 获取id池详细信息
     * 不保证时效性
     *
     * @return
     */
    public Map getIdPool() {
        return new ConcurrentHashMap<>(poolRegister);
    }
}
