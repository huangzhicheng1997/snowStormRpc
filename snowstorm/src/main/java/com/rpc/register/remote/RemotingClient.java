package com.rpc.register.remote;

import com.rpc.register.codec.MessageDecode;
import com.rpc.register.codec.MessageEncode;
import com.rpc.register.common.MessageType;
import com.rpc.register.core.ChannelWrapper;
import com.rpc.register.core.ResponseFuture;
import com.rpc.register.exceptions.SendMsgException;
import com.rpc.register.protocol.MessageProtocol;
import com.rpc.register.protocol.header.BaseMessage;
import com.rpc.register.util.IpUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: hzc
 * @Date: 2020/03/16  22:21
 * @Description:
 */
public class RemotingClient extends AbstractRemoting {
    private Bootstrap bootstrap = null;
    private EventLoopGroup boss = null;
    private DefaultEventExecutorGroup defaultEventExecutorGroup = new DefaultEventExecutorGroup(8);
    private Lock remotingClientLock = new ReentrantLock();
    private ClientHandler clientHandler;
    private MessageEncode messageEncode;
    private MessageDecode messageDecode;

    /**
     * 存储ip 与 Channel的映射关系
     */
    private ConcurrentMap<String, ChannelWrapper> channelTable = new ConcurrentHashMap<>();

    public void start() {
        this.messageDecode = new MessageDecode(1024 * 1024, 4, 4, 0, 0);
        this.messageEncode = new MessageEncode();
        this.clientHandler = new ClientHandler();
        bootstrap = new Bootstrap();
        boss = new NioEventLoopGroup(1);
        bootstrap.group(boss).channel(NioSocketChannel.class)
                .option(ChannelOption.SO_SNDBUF, 65535)
                .option(ChannelOption.SO_RCVBUF, 65535)
                .option(ChannelOption.TCP_NODELAY, false)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(final SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(defaultEventExecutorGroup, new MessageDecode(1024 * 1024, 4, 4, 0, 0))
                                .addLast(defaultEventExecutorGroup, new MessageEncode())
                                .addLast(defaultEventExecutorGroup, new ClientHandler())
                                .addLast(defaultEventExecutorGroup, new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                        //掉线 清除channelTable
                                        String addr = ctx.channel().remoteAddress().toString();
                                        channelTable.remove(addr);
                                    }
                                });
                    }
                });
        removeDisCardMsg();

    }

    private Channel createChannel(String addr) throws InterruptedException {
        //提高效率
        ChannelWrapper channelWrapper = channelTable.get(addr);
        if (null != channelWrapper && channelWrapper.isOK()) {
            return channelWrapper.getChannel();
        }

        if (remotingClientLock.tryLock(3000L, TimeUnit.MILLISECONDS)) {
            try {
                boolean needConnect;
                channelWrapper = channelTable.get(addr);
                if (null != channelWrapper) {
                    //channel不为空而且channel为active状态
                    if (channelWrapper.isOK()) {
                        return channelWrapper.getChannel();
                        //正在连接状态
                    } else if (!channelWrapper.getChannelFuture().isDone()) {
                        needConnect = false;
                        //已关闭或连接失败的channelFuture 从列表中删除同时重新建立一个连接
                    } else {
                        channelTable.remove(addr);
                        needConnect = true;
                    }

                } else {
                    needConnect = true;
                }

                if (needConnect) {
                    ChannelFuture channelFuture = bootstrap.connect(IpUtil.string2SocketAddress(addr));
                    channelWrapper = new ChannelWrapper(channelFuture);
                    channelTable.put(addr, channelWrapper);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                remotingClientLock.unlock();
            }

            if (null != channelWrapper) {
                //等待3秒（3秒为bootstrap设置的tcp连接超时时间)
                if (channelWrapper.getChannelFuture().awaitUninterruptibly(3000L)) {
                    if (channelWrapper.isOK()) {
                        return channelWrapper.getChannel();
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            }


        }
        return null;

    }


    public BaseMessage sendRequest(String addr, BaseMessage baseMessage) throws InterruptedException {
        Channel channel = createChannel(addr);
        if (null != channel) {
            ResponseFuture responseFuture = new ResponseFuture(baseMessage.getMessageId(), channel);
            responseTable.put(baseMessage.getMessageId(), responseFuture);
            MessageProtocol message = MessageProtocol.createMessage(MessageType.BUSSINESS_MSG_REQ, baseMessage);
            channel.writeAndFlush(message).addListener(future -> {
                if (future.isSuccess()) {
                    responseFuture.setSendRequestOk(true);
                } else {
                    responseFuture.setSendRequestOk(false);
                }
            });

            return responseFuture.getMessageSync(10);
        } else {
            throw new SendMsgException("create channel error");
        }
    }


    /**
     * 关闭
     */
    public void remotingClientShutDown() {
        defaultEventExecutorGroup.shutdownGracefully();
        boss.shutdownGracefully();
    }


    class ClientHandler extends SimpleChannelInboundHandler<MessageProtocol> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, MessageProtocol msg) throws Exception {
            processMessage(ctx, msg);
        }
    }

}
