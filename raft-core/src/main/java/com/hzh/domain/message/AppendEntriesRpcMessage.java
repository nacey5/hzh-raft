package com.hzh.domain.message;

import com.hzh.domain.node.NodeId;
import com.hzh.rpc.nio.Channel;

/**
 * @ClassName AppendEntriesRpcMessage
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/2 11:51
 * @Version 0.0.1
 **/
public class AppendEntriesRpcMessage extends AbstractRpcMessage<AppendEntriesRpc>{
    public AppendEntriesRpcMessage(AppendEntriesRpc rpc, NodeId sourceNodeId) {
        super(rpc, sourceNodeId,null);
    }
    public AppendEntriesRpcMessage(AppendEntriesRpc rpc, NodeId sourceNodeId, Channel channel) {
        super(rpc, sourceNodeId, channel);
    }
}
