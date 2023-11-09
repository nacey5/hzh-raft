package com.hzh.rpc.nio.handler;

import com.google.common.eventbus.EventBus;
import com.hzh.domain.message.*;
import com.hzh.domain.node.NodeId;
import com.hzh.rpc.nio.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;

/**
 * @ClassName AbstractHandler
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/8 17:03
 * @Version 0.0.1
 **/
public class AbstractHandler extends ChannelDuplexHandler {
    private static final Logger logger = LoggerFactory.getLogger(AbstractHandler.class);
    protected final EventBus eventBus;
    NodeId remoteId;
    //Channel in RPC component. Non-Netty Channel
    protected Channel channel;
    //The last iterated AppendEntriesRpc message
    private AppendEntriesRpc lastAppendEntriesRpc;

    AbstractHandler(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        assert remoteId != null;
        assert channel != null;
        //Forward the message after determining the type
        if (msg instanceof RequestVoteRpc) {
            RequestVoteRpc rpc = (RequestVoteRpc) msg;
            eventBus.post(new RequestVoteRpcMessage(rpc, remoteId, channel));
        } else if (msg instanceof RequestVoteResult) {
            eventBus.post(msg);
        } else if (msg instanceof AppendEntriesResult) {
            AppendEntriesResult result = (AppendEntriesResult) msg;
            if (lastAppendEntriesRpc == null) {
                logger.warn("no last append entries rpc");
            } else {
                eventBus.post(new AppendEntriesResultMessage(result, remoteId, lastAppendEntriesRpc));
                lastAppendEntriesRpc = null;
            }
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof AppendEntriesRpc) {
            lastAppendEntriesRpc = (AppendEntriesRpc) msg;
        }
        super.write(ctx,msg,promise);
    }
}
