package com.hzh.domain.message;

import com.google.common.base.Preconditions;
import com.hzh.domain.node.NodeId;

import javax.annotation.Nonnull;

/**
 * @ClassName AppendEntriesResultMessage
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/2 11:53
 * @Version 0.0.1
 **/
public class AppendEntriesResultMessage {

    private final AppendEntriesResult result;
    private final NodeId sourceNodeId;

    // TODO remove rpc, just lastEntryIndex required, or move to replicating state?
    private final AppendEntriesRpc rpc;

    public AppendEntriesResultMessage(AppendEntriesResult result, NodeId sourceNodeId, @Nonnull AppendEntriesRpc rpc) {
        Preconditions.checkNotNull(rpc);
        this.result = result;
        this.sourceNodeId = sourceNodeId;
        this.rpc = rpc;
    }
    public AppendEntriesResult get() {
        return result;
    }

    public NodeId getSourceNodeId() {
        return sourceNodeId;
    }

    public AppendEntriesRpc getRpc() {
        return rpc;
    }
}
