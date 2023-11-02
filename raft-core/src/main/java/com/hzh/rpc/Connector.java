package com.hzh.rpc;

import com.hzh.domain.message.AppendEntriesResult;
import com.hzh.domain.message.AppendEntriesRpc;
import com.hzh.domain.message.RequestVoteResult;
import com.hzh.domain.message.RequestVoteRpc;
import com.hzh.domain.node.NodeEndpoint;

import java.util.Collection;

/**
 * @ClassName Connector
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/1 16:37
 * @Version 0.0.1
 **/
public interface Connector {
    //init
    void initialize();

    //Send RequestVote messages to multiple nodes
    void sendRequestVote(RequestVoteRpc rpc ,
                         Collection<NodeEndpoint> destinationEndpoints) ;

    //Reply RequestVote to a single node
    void replyRequestVote(RequestVoteResult result, NodeEndpoint destinationEndpoint);

    //Send Append entries messages to a single node
    void sendAppendEntries(AppendEntriesRpc rpc, NodeEndpoint destinationEndpoint);

    //Return Append dEnt es results to a single node
    void replyAppendEntries(AppendEntriesResult rpc, NodeEndpoint destinationEndpoint);

    void close();
}
