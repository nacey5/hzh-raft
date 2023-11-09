package com.hzh.rpc.nio.handler;

import com.google.common.eventbus.EventBus;
import com.hzh.domain.node.NodeId;
import com.hzh.rpc.nio.NioChannel;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName ToRemoteHandler
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/8 19:18
 * @Version 0.0.1
 **/
public class ToRemoteHandler extends AbstractHandler{
    private static final Logger logger = LoggerFactory.getLogger(ToRemoteHandler.class);
    private final NodeId selfNodeId;
    public ToRemoteHandler(EventBus eventBus, NodeId remoteId, NodeId selfNodeId) {
        super(eventBus);
        this.remoteId=remoteId;
        this.selfNodeId=selfNodeId;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.write(selfNodeId);
        channel=new NioChannel(ctx.channel());
    }
}
