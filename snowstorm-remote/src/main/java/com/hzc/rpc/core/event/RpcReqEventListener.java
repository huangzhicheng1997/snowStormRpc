package com.hzc.rpc.core.event;

import java.util.Random;
import java.util.concurrent.*;

/**
 * @author: hzc
 * @Date: 2020/03/30  11:56
 * @Description:
 */
public class RpcReqEventListener implements RpcEventListener {

    /**
     * 双端队列
     */
    private static final ConcurrentMap<Integer/*threadId*/, BlockingDeque<CallerHandleEvent>> callEventDequeTable = new ConcurrentHashMap<>();

    /**
     * 主线程组
     */
    private ConcurrentMap<Integer/*threadId*/, Thread> mainThreadGroup = new ConcurrentHashMap<>();

    /**
     * 窃取线程组
     */
    private ExecutorService stealThreadGroup;

    /**
     * 窃取型线程数
     */
    private Integer stealThreadNum;

    private RpcService rpcService;


    /**
     * maxThread-queueCapacity 得到的为窃取线程组的线程数
     */
    public RpcReqEventListener(Integer queueNumbers, Integer queueCapacity, Integer maxThread) {
        if (queueNumbers == 0 || queueCapacity == 0) {
            throw new RuntimeException("创建失败");
        }
        if (maxThread < queueNumbers) {
            throw new RuntimeException("参数错误");
        }

        /*
         * 每个队列都有一个主线程进行消费。
         * （maxThread - queueNumbers） 为额外提供的线程，消费队头，和主消费线程配合进行密取型消费
         *
         */
        stealThreadNum = maxThread - queueNumbers;

        for (int i = 0; i < queueNumbers; i++) {
            LinkedBlockingDeque<CallerHandleEvent> callEventDeque = new LinkedBlockingDeque<>(queueCapacity);
            //线程绑定队列
            Thread thread = new Thread(new EventTask(this, callEventDeque));
            mainThreadGroup.put(i, thread);
            callEventDequeTable.put(i, callEventDeque);
        }
        stealThreadGroup = Executors.newFixedThreadPool(maxThread - queueNumbers);
    }

    @Override
    public void eventExecuteAndNotify() {
        start();
    }

    /**
     * 随机发布事件
     *
     * @param callerHandleEvent
     * @throws InterruptedException
     */
    public static void publishCallEvent(CallerHandleEvent callerHandleEvent) throws InterruptedException {
        Random random = new Random();
        int randomInt = random.nextInt(callEventDequeTable.size() - 1);

        BlockingDeque<CallerHandleEvent> callerHandleEvents = callEventDequeTable.get(randomInt);

        callerHandleEvents.put(callerHandleEvent);
    }

    /**
     * 开启主线程组，以及窃取线程组
     */
    private void start() {
        mainThreadGroup.forEach((key, val) -> {
            val.start();
        });
        for (int i = 0; i < stealThreadNum; i++) {
            stealThreadGroup.submit(new StealTask());
        }
    }

    public RpcService getRpcService() {
        return rpcService;
    }

    public void setRpcService(RpcService rpcService) {
        this.rpcService = rpcService;
    }

    /**
     * 事件消费任务
     */
    private class EventTask implements Runnable {
        private RpcReqEventListener rpcReqEventListener;

        private BlockingDeque<CallerHandleEvent> deque;


        public EventTask(RpcReqEventListener rpcReqEventListener, BlockingDeque<CallerHandleEvent> deque) {
            this.rpcReqEventListener = rpcReqEventListener;
            this.deque = deque;
        }

        @Override
        public void run() {
            for (; ; ) {
                CallerHandleEvent callerHandleEvent = null;
                try {
                    callerHandleEvent = deque.takeLast();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                rpcService.executorService(callerHandleEvent);
            }
        }
    }


    /**
     * 窃取型任务
     */
    private class StealTask implements Runnable {
        @Override
        public void run() {
            for (; ; ) {
                Random random = new Random();
                int randomInt = random.nextInt(callEventDequeTable.size() - 1);
                BlockingDeque<CallerHandleEvent> callerHandleEvents = callEventDequeTable.get(randomInt);
                CallerHandleEvent callerHandleEvent = callerHandleEvents.pollFirst();
                if (callerHandleEvent==null){
                    continue;
                }
                rpcService.executorService(callerHandleEvent);
            }

        }
    }
}
