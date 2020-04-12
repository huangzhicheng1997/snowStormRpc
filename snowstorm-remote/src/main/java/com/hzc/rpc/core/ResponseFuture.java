package com.hzc.rpc.core;

import com.hzc.rpc.protocol.header.BaseMessage;
import io.netty.channel.Channel;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author: hzc
 * @Date: 2020/03/18  18:27
 * @Description:
 */
public class ResponseFuture {
    /**
     * 时间戳
     */
    private final String responseId;

    /**
     *
     */
    private final Channel processChannel;

    private volatile boolean isSendRequestSuccess;

    private volatile BaseMessage baseMessage;

    private volatile boolean isDiscard;

    /**
     * 栅拦 每次put 栅拦置为0 ，get消息时
     */
    private final CountDownLatch countDownLatch = new CountDownLatch(1);

    public ResponseFuture(String responseId, Channel processChannel) {
        this.responseId = responseId;
        this.processChannel = processChannel;
    }


    public void putBaseMessage(BaseMessage baseMessage) {
        this.baseMessage = baseMessage;
        countDownLatch.countDown();
        isDiscard = true;
    }

    public BaseMessage getMessageSync(Integer timeout) throws InterruptedException {
        countDownLatch.await(timeout, TimeUnit.SECONDS);
        //方法被调用就置这个response为废弃状态
        return baseMessage;
    }

    public BaseMessage getMessageAtOnce() {
        return baseMessage;
    }

    public void setSendRequestOk(boolean isSuccess) {
        this.isSendRequestSuccess = isSuccess;
    }

    public boolean isSendRequestOk() {
        return isSendRequestSuccess;
    }

    public boolean isShouldDiscard() {
        return isDiscard;
    }
}
