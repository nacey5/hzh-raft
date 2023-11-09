package com.hzh.rpc.nio;

import com.google.common.eventbus.EventBus;
import com.hzh.domain.message.AppendEntriesResult;
import com.hzh.domain.message.AppendEntriesRpc;
import com.hzh.domain.message.RequestVoteResult;
import com.hzh.domain.message.RequestVoteRpc;
import com.hzh.domain.node.NodeEndpoint;
import com.hzh.domain.node.NodeId;
import com.hzh.rpc.Connector;
import com.hzh.rpc.nio.handler.FromRemoteHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.hzh.exception.ConnectException;

import java.util.Collection;

/**
 * @ClassName NioConnector
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/8 15:33
 * @Version 0.0.1
 **/
public class NioConnector implements Connector {
    private final Logger logger = LoggerFactory.getLogger(NioConnector.class);
    //Selector's thread pool, here is a single thread
    private final NioEventLoopGroup bossNioEventLoopGroup = new NioEventLoopGroup(1);
    //IO thread pool, here is a fixed number of multi-threads
    private final NioEventLoopGroup workerNioEventLoopGroup;
    //Whether the thread pool is shared with upper-layer services, etc.
    private final boolean workerGroupShared;
    private final EventBus eventBus;
    //the node port
    private final int port;
    // inbound channel group
    private final InboundChannelGroup inboundChannelGroup = new InboundChannelGroup();
    //outbound channel group
    private final OutBoundChannelGroup outBoundChannelGroup;

    public NioConnector(NioEventLoopGroup workerNioEventLoopGroup, boolean workerGroupShared, NodeId selfNodeId, EventBus eventBus, int port) {
        this.workerNioEventLoopGroup = workerNioEventLoopGroup;
        this.workerGroupShared = workerGroupShared;
        this.eventBus = eventBus;
        this.port = port;
        outBoundChannelGroup = new OutBoundChannelGroup(workerNioEventLoopGroup, eventBus, selfNodeId);
    }


    @Override
    public void initialize() {
        ServerBootstrap serverBootstrap = new ServerBootstrap()
                .group(bossNioEventLoopGroup, workerNioEventLoopGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new Encoder());
                        ch.pipeline().addLast(new Encoder());
                        ch.pipeline().addLast(
                                new FromRemoteHandler(eventBus, inboundChannelGroup));
                    }
                });
        logger.debug("node listen on port {}",port);
        try{
            serverBootstrap.bind(port).sync();
        } catch (InterruptedException e) {
            throw new ConnectException("failed to bind port",e);
        }
    }

    @Override
    public void sendRequestVote(RequestVoteRpc rpc, Collection<NodeEndpoint> destinationEndpoints) {
        for (NodeEndpoint endpoint : destinationEndpoints) {
            try{
                getChannel(endpoint).writeRequestVoteRpc(rpc);
            }catch (Exception e){
                logger.warn("fail to send RequestVoteRpc",e);
            }
        }
    }

    private Channel getChannel(NodeEndpoint endpoint) {
        return outBoundChannelGroup.getOrConnect(endpoint.getId(),endpoint.getAddress());
    }

    @Override
    public void replyRequestVote(RequestVoteResult result, NodeEndpoint destinationEndpoint) {

    }

    @Override
    public void sendAppendEntries(AppendEntriesRpc rpc, NodeEndpoint destinationEndpoint) {

    }

    @Override
    public void replyAppendEntries(AppendEntriesResult rpc, NodeEndpoint destinationEndpoint) {

    }

    @Override
    public void resetChannels() {

    }

    @Override
    public void close() {
        logger.debug("close connector");
        inboundChannelGroup.closeAll();
        outBoundChannelGroup.closeAll();
        bossNioEventLoopGroup.shutdownGracefully();
        if (!workerGroupShared){
            workerNioEventLoopGroup.shutdownGracefully();
        }
    }

}
