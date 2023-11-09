package com.hzh.domain.message;

import com.hzh.domain.node.NodeId;
import com.hzh.rpc.nio.Channel;

/**
 * @ClassName AbstractRpcMessage
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/2 11:43
 * @Version 0.0.1
 **/
public abstract class AbstractRpcMessage<T> {
    private final T rpc;
    private final NodeId sourceNodeId;
    private final Channel channel;

//    private final Channel channel;

    AbstractRpcMessage(T rpc, NodeId sourceNodeId, Channel channel) {
        this.rpc = rpc;
        this.sourceNodeId = sourceNodeId;
        this.channel = channel;
    }

    public T get() {
        return this.rpc;
    }

    public NodeId getSourceId() {
        return sourceNodeId;
    }
}
