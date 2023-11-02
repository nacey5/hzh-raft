package com.hzh.domain.message;

import com.hzh.domain.node.NodeId;

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

//    private final Channel channel;

    AbstractRpcMessage(T rpc, NodeId sourceNodeId) {
        this.rpc = rpc;
        this.sourceNodeId = sourceNodeId;
    }

    public T get() {
        return this.rpc;
    }

    public NodeId getSourceId() {
        return sourceNodeId;
    }
}
