package com.hzh.rpc.nio.handler;

import com.google.common.eventbus.EventBus;
import com.hzh.domain.node.NodeId;
import com.hzh.rpc.nio.InboundChannelGroup;
import com.hzh.rpc.nio.NioChannel;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName FromRemoteHandler
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/8 17:02
 * @Version 0.0.1
 **/
public class FromRemoteHandler extends AbstractHandler{
    private static final Logger logger = LoggerFactory.getLogger(FromRemoteHandler.class);
    private final InboundChannelGroup channelGroup;

    public FromRemoteHandler(EventBus eventBus, InboundChannelGroup channelGroup) {
        super(eventBus);
        this.channelGroup = channelGroup;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof NodeId) {
            remoteId = (NodeId) msg;
            NioChannel nioChannel = new NioChannel(ctx.channel());
            channel = nioChannel;
            channelGroup.add(remoteId, nioChannel);
            return;
        }

        logger.debug("receive {} from {}", msg, remoteId);
        super.channelRead(ctx, msg);
    }
}
