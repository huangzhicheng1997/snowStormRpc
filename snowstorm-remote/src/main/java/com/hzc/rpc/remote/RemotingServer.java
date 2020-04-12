package com.hzc.rpc.remote;

import com.hzc.rpc.config.RouterConfig;
import com.hzc.rpc.handler.ServerConnectionManageHandler;
import com.hzc.rpc.protocol.MessageProtocol;
import com.hzc.rpc.protocol.header.BaseMessage;
import com.hzc.rpc.codec.MessageDecode;
import com.hzc.rpc.codec.MessageEncode;
import com.hzc.rpc.common.MessageType;
import com.hzc.rpc.common.ServerType;
import com.hzc.rpc.config.DispatcherConfig;
import com.hzc.rpc.core.ResponseFuture;
import com.hzc.rpc.handler.CloseChannelStrategy;
import com.hzc.rpc.util.SystemUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: hzc
 * @Date: 2020/03/02  18:12
 * @Description:
 */
public class RemotingServer extends AbstractRemoting {
    private ServerBootstrap serverBootstrap;
    private EventLoopGroup acceptor;
    private EventLoopGroup ioWorker;
    private DefaultEventExecutorGroup defaultEventExecutorGroup;
    private MessageEncode messageEncode;
    private CloseChannelStrategy closeChannelStrategy;
    private ServerHandler serverHandler;
    private ServerType serverType;

    public RemotingServer() {
        serverBootstrap = new ServerBootstrap();
        messageEncode = new MessageEncode();
        this.serverHandler = new ServerHandler();
        if (SystemUtil.isLinux()) {
            //todo 配置
            acceptor = new EpollEventLoopGroup(1, new ThreadFactory() {
                private AtomicInteger threadNum = new AtomicInteger(0);

                public Thread newThread(Runnable r) {
                    return new Thread(r, String.format("acceptorThread-%d", threadNum.incrementAndGet()));
                }
            });
            ioWorker = new EpollEventLoopGroup(3, new ThreadFactory() {
                private AtomicInteger threadNum = new AtomicInteger(0);

                public Thread newThread(Runnable r) {
                    return new Thread(r, String.format("ioThread-%d", threadNum.incrementAndGet()));
                }
            });
        } else {
            acceptor = new NioEventLoopGroup(1, new ThreadFactory() {
                private AtomicInteger threadNum = new AtomicInteger(0);

                public Thread newThread(Runnable r) {
                    return new Thread(r, String.format("acceptorThread-%d", threadNum.incrementAndGet()));
                }
            });
            ioWorker = new NioEventLoopGroup(3, new ThreadFactory() {
                private AtomicInteger threadNum = new AtomicInteger(0);

                public Thread newThread(Runnable r) {
                    return new Thread(r, String.format("ioThread-%d", threadNum.incrementAndGet()));
                }
            });
        }
        defaultEventExecutorGroup = new DefaultEventExecutorGroup(8, new ThreadFactory() {
            private AtomicInteger threadNum = new AtomicInteger(0);

            public Thread newThread(Runnable r) {
                return new Thread(r, String.format("scheduledThread-%d", threadNum.incrementAndGet()));
            }
        });
    }

    public void start() {

        serverBootstrap.group(acceptor, ioWorker).channel(SystemUtil.isLinux() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY, false)
                //端口复用
                .childOption(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.SO_RCVBUF, 65535)
                .childOption(ChannelOption.SO_SNDBUF, 65535)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .localAddress(new InetSocketAddress(serverType.equals(ServerType.ROUTER)? RouterConfig.getInstance().getServerPort(): DispatcherConfig.getInstance().getServerPort()))
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(defaultEventExecutorGroup, new MessageDecode(1024 * 1024, 4, 4, 0, 0))
                                .addLast(defaultEventExecutorGroup, new MessageEncode())
                                .addLast(defaultEventExecutorGroup, new ServerConnectionManageHandler(closeChannelStrategy))
                                .addLast(defaultEventExecutorGroup, serverHandler)

                        ;

                    }
                });
        ChannelFuture channelFuture = serverBootstrap.bind();
        channelFuture.addListener(future -> {
            if (channelFuture.isSuccess()) {
                channelFuture.channel().closeFuture().addListener(future1 -> {
                    this.acceptor.shutdownGracefully();
                    this.ioWorker.shutdownGracefully();
                    this.defaultEventExecutorGroup.shutdownGracefully();
                });
            }
        });
        removeDisCardMsg();

    }

    public BaseMessage sendMessage(Channel channel, BaseMessage baseMessage) throws InterruptedException {
        if (null != channel) {
            //生成responseFuture
            ResponseFuture responseFuture = new ResponseFuture(baseMessage.getMessageId(), channel);
            responseTable.put(baseMessage.getMessageId(), responseFuture);
            ChannelFuture channelFuture = channel.writeAndFlush(MessageProtocol.createMessage(MessageType.BUSSINESS_MSG_REQ, baseMessage));
            channelFuture.addListener(future -> {
                if (future.isSuccess()) {
                    responseFuture.setSendRequestOk(true);
                } else {
                    responseFuture.setSendRequestOk(false);
                }
            });
            return responseFuture.getMessageSync(1000);
        }
        return null;
    }

    @ChannelHandler.Sharable
    class ServerHandler extends SimpleChannelInboundHandler<MessageProtocol> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, MessageProtocol msg) throws Exception {
            processMessage(ctx, msg);
        }
    }


    public void setCloseChannelStrategy(CloseChannelStrategy closeChannelStrategy) {
        this.closeChannelStrategy = closeChannelStrategy;
    }

    public void setServerType(ServerType serverType) {
        this.serverType = serverType;
    }
}
