package com.hzh.rpc.nio;

import com.hzh.domain.node.NodeId;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @ClassName InboundChannelGroup
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/8 20:22
 * @Version 0.0.1
 **/
public class InboundChannelGroup {
    private final Logger logger = LoggerFactory.getLogger(InboundChannelGroup.class);
    private final List<NioChannel> channels =new CopyOnWriteArrayList<>();

    // add the  inbound connect
    public void add(NodeId remoteId,NioChannel channel){
        logger.debug("channel INBOUND-{} connected",remoteId);
        channel.getDelegate().closeFuture().addListener((ChannelFutureListener)future->{
           //remove when the connection close
           logger.debug("channel INBOUND-{} disconnected",remoteId);
           remove(channel);
        });
    }

    private void remove(NioChannel channel) {
        channels.remove(channel);
    }

    void closeAll(){
        logger.debug("close all inbound channels");
        for (NioChannel channel : channels) {
            channel.close();
        }
    }
}
