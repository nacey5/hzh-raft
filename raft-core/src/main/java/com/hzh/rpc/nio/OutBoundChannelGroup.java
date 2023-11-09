package com.hzh.rpc.nio;

import com.google.common.eventbus.EventBus;
import com.hzh.domain.node.NodeId;
import com.hzh.exception.ChannelConnectException;
import com.hzh.rpc.Address;
import com.hzh.rpc.nio.handler.ToRemoteHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * @ClassName OutBoundChannelGroup
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/8 19:09
 * @Version 0.0.1
 **/
public class OutBoundChannelGroup {

    private final Logger logger = LoggerFactory.getLogger(OutBoundChannelGroup.class);
    private final EventLoopGroup workerGroup;
    private final EventBus eventBus;
    private final NodeId selfNodeId;
    private final ConcurrentMap<NodeId, Future<NioChannel>> channelMap = new ConcurrentHashMap<>();

    public OutBoundChannelGroup(EventLoopGroup workerGroup, EventBus eventBus, NodeId selfNodeId) {
        this.workerGroup = workerGroup;
        this.eventBus = eventBus;
        this.selfNodeId = selfNodeId;
    }

    private NioChannel connect(NodeId nodeId, Address address) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap()
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new Decoder());
                        ch.pipeline().addLast(new Encoder());
                        ch.pipeline().addLast(new ToRemoteHandler(eventBus, nodeId, selfNodeId));
                    }
                });
        ChannelFuture future = bootstrap.connect(address.getHost(), address.getPort()).sync();
        //Sync waiting for connection to complete
        if (!future.isSuccess()) {
            throw new ChannelException("fail to connect", future.cause());
        }
        logger.debug("channel OUTBOUND-{} connected", nodeId);
        Channel nettyChannel = future.channel();
        //Delete from the group after the linkage is closed
        nettyChannel.closeFuture().addListener((ChannelFutureListener) cf -> {
            logger.debug("channel OUTBOUND-{} disConnected", nodeId);
            channelMap.remove(nodeId);
        });
        return new NioChannel(nettyChannel);
    }

    public NioChannel getOrConnect(NodeId nodeId, Address address) {
        Future<NioChannel> future = channelMap.get(nodeId);
        if (future == null) {
            FutureTask<NioChannel> newFuture = new FutureTask<>(() -> connect(nodeId, address));
            future = channelMap.putIfAbsent(nodeId, newFuture);
            if (future == null) {
                future = newFuture;
                //Execute the connect method in the current thread
                newFuture.run();
            }
        }
        try {
            return future.get();
        } catch (Exception e) {
            channelMap.remove(nodeId);
            if (e instanceof ExecutionException) {
                Throwable cause = e.getCause();
                throw new ChannelConnectException(
                        "fail to get channel to node " + nodeId + ", cause " + cause.getMessage(), cause
                );
            }
            throw new ChannelException("failed to get channel to node " + nodeId, e);
        }
    }

    void closeAll() {
        logger.debug("close all outbound channels");
        channelMap.forEach((nodeId, nioChannelFuture) -> {
            try {
                nioChannelFuture.get().close();
            } catch (Exception e) {
                logger.warn("failed to close", e);
            }
        });
    }

}
