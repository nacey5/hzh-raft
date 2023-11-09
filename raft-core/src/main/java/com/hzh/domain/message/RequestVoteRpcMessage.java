package com.hzh.domain.message;

import com.hzh.domain.node.NodeId;
import com.hzh.rpc.nio.Channel;

/**
 * @ClassName RequestVoteRpcMessage
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/2 11:44
 * @Version 0.0.1
 **/
public class RequestVoteRpcMessage extends AbstractRpcMessage<RequestVoteRpc>{
    public RequestVoteRpcMessage(RequestVoteRpc rpc, NodeId sourceNodeId) {
        super(rpc, sourceNodeId,null);
    }
    public RequestVoteRpcMessage(RequestVoteRpc rpc, NodeId sourceNodeId, Channel channel) {
        super(rpc, sourceNodeId, channel);
    }
}
