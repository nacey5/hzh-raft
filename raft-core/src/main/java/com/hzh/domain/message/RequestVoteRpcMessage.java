package com.hzh.domain.message;

import com.hzh.domain.node.NodeId;

/**
 * @ClassName RequestVoteRpcMessage
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/2 11:44
 * @Version 0.0.1
 **/
public class RequestVoteRpcMessage extends AbstractRpcMessage<RequestVoteRpc>{
    public RequestVoteRpcMessage(RequestVoteRpc rpc, NodeId sourceNodeId) {
        super(rpc, sourceNodeId);
    }
}
