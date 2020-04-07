package com.rpc.register.remote;

import com.rpc.register.common.MessageType;
import com.rpc.register.core.Pair;
import com.rpc.register.core.ResponseFuture;
import com.rpc.register.handler.RequestProcessor;
import com.rpc.register.protocol.MessageProtocol;
import com.rpc.register.protocol.header.BaseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

import java.util.concurrent.*;

/**
 * @author: hzc
 * @Date: 2020/03/18  21:30
 * @Description:
 */
public class AbstractRemoting {

    static final ConcurrentMap<String, ResponseFuture> responseTable = new ConcurrentHashMap<>();

    //不同code的消息对应处理类map
    private static final ConcurrentMap<Integer /*request类型Message的code*/, Pair<RequestProcessor /*对应request的处理程序*/, ExecutorService>>
            requestMsgProcessorTable = new ConcurrentHashMap<>();

    public void registerProcesser(Integer messageCode, Pair<RequestProcessor, ExecutorService> pair) {
        requestMsgProcessorTable.put(messageCode, pair);
    }

    private Pair<RequestProcessor, ExecutorService> getRequestProcessor(Integer messageCode) {
        return requestMsgProcessorTable.get(messageCode);
    }

    void processMessage(ChannelHandlerContext ctx, MessageProtocol messageProtocol) {
        if (messageProtocol.getType() == MessageType.BUSSINESS_MSG_RES.getType()) {
            processRes(ctx, messageProtocol);
        } else {
            processReq(ctx, messageProtocol);
        }
    }

    private void processReq(ChannelHandlerContext ctx, MessageProtocol messageProtocol) {
        BaseMessage baseMessage = messageProtocol.getContent();
        //获取对应消息的处理程序
        Pair<RequestProcessor, ExecutorService> pair = getRequestProcessor(baseMessage.getmCode());
        RequestProcessor requestProcessor = pair.getObject1();
        ExecutorService executorService = pair.getObject2();
        executorService.submit(() -> {
            requestProcessor.requestHandle(ctx, messageProtocol);
        });
    }

    /**
     * 处理返回值（把返回的消息设置到future中推到responseTable 触发future的get监听程序）
     *
     * @param ctx
     * @param messageProtocol
     */
    private void processRes(ChannelHandlerContext ctx, MessageProtocol messageProtocol) {
        BaseMessage baseMessage = messageProtocol.getContent();
        ResponseFuture responseFuture = responseTable.get(baseMessage.getMessageId());
        responseFuture.setSendRequestOk(true);
        responseFuture.putBaseMessage(baseMessage);
    }


    /**
     * 每10秒剔除失效response
     */
    void removeDisCardMsg() {
        new Thread(new Runnable() {
            private Semaphore await = new Semaphore(0);

            @Override
            public void run() {
                for (; ; ) {
                    try {
                        await.tryAcquire(10, TimeUnit.SECONDS);
                    } catch (InterruptedException ignored) {
                    }
                    responseTable.forEach((key, responseFuture) -> {
                        if (responseFuture.isShouldDiscard()) {
                            responseTable.remove(key);
                        }
                    });
                }
            }
        });

    }

}
