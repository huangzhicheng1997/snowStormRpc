package com.hzc.rpc.util;

import com.google.common.util.concurrent.*;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author: hzc
 * @Date: 2020/04/01  14:43
 * @Description:
 */
public class ParallelExecutor {

    /**
     * 并发执行非耦合任务
     *
     * @param tasks               Callable型任务
     * @param parallelThreadGroup 并行执行线程组
     * @param maxTimeout          最大超时时间
     * @param timeUnit
     * @return
     * @throws InterruptedException
     */
    public static List<Object> parallelExecute(List<Callable<?>> tasks, ExecutorService parallelThreadGroup, Long maxTimeout, TimeUnit timeUnit) throws InterruptedException {
        if (CollectionUtils.isEmpty(tasks)) {
            return null;
        }
        CountDownLatch countDownLatch = new CountDownLatch(tasks.size());

        List<Object> resultList = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            resultList.add(null);
        }

        ListeningExecutorService listeningExecutorService = MoreExecutors.listeningDecorator(parallelThreadGroup);
        for (int i = 0; i < tasks.size(); i++) {
            ListenableFuture<?> listenableFuture = listeningExecutorService.submit(tasks.get(i));
            final int index = i;
            listenableFuture.addListener(() -> {
                try {
                    resultList.set(index, listenableFuture.get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            }, listeningExecutorService);
        }

        boolean flag = countDownLatch.await(maxTimeout, timeUnit);
        if (!flag) {
            return null;
        }
        return resultList;
    }

    public static void main(String[] args) throws InterruptedException {
        Callable<String> callable = () -> {
            Thread.sleep(3000);
            return "1";
        };
        Callable<Integer> callable2 = () -> {
            Thread.sleep(3000);
            return 2;
        };
        List<Callable<?>> callables = new ArrayList<>();
        callables.add(callable);
        callables.add(callable2);
        List list = parallelExecute(callables, Executors.newFixedThreadPool(2), 4L, TimeUnit.SECONDS);
        System.out.println(list.get(0));
    }
}
