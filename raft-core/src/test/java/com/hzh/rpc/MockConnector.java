package com.hzh.rpc;

import com.hzh.domain.message.AppendEntriesResult;
import com.hzh.domain.message.AppendEntriesRpc;
import com.hzh.domain.message.RequestVoteResult;
import com.hzh.domain.message.RequestVoteRpc;
import com.hzh.domain.node.NodeEndpoint;
import com.hzh.domain.node.NodeId;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @ClassName MockConnector
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/2 13:34
 * @Version 0.0.1
 **/
public class MockConnector implements Connector {

    @Data
    public static class Message {
        //rpc message
        private Object rpc;
        //point node
        private NodeId destinationNodeId;
        //result
        private Object result;

    }

    private LinkedList<Message> messages = new LinkedList<>();

    //get the Last message
    public Message getLastMessage() {
        return messages.isEmpty() ? null : messages.getLast();
    }
    //get the last message or the empty message
    private Message getLAstMessageOrDefault(){
        return messages.isEmpty() ? new Message() : messages.getLast();
    }
    //get the last result
    public Object getResult(){
        return getLAstMessageOrDefault().result;
    }
    //get the lastMessage's point node
    public NodeId getDestinationNodeId(){
        return getLAstMessageOrDefault().getDestinationNodeId();
    }
    // get the message count
    public int getMessageCount(){
        return messages.size();
    }
    //get All messages
    public List<Message> getMessages(){
        return new ArrayList<>(messages);
    }
    //clear messages
    public void clearMessage(){
        messages.clear();
    }
    @Override
    public void initialize() {

    }

    @Override
    public void sendRequestVote(RequestVoteRpc rpc, Collection<NodeEndpoint> destinationEndpoints) {
        //对于事 目标节点 这里没有完全处理
        Message m = new Message();
        m.setRpc(rpc);
        messages.add(m);
    }

    @Override
    public void replyRequestVote(RequestVoteResult result, NodeEndpoint destinationEndpoint) {
        Message m = new Message();
        m.setResult(result);
        m.setDestinationNodeId(destinationEndpoint.getId());
        messages.add(m);
    }

    @Override
    public void sendAppendEntries(AppendEntriesRpc rpc, NodeEndpoint destinationEndpoint) {
        Message m = new Message();
        m.setRpc(rpc);
        m.setDestinationNodeId(destinationEndpoint.getId());
        messages.add(m);
    }

    @Override
    public void replyAppendEntries(AppendEntriesResult result, NodeEndpoint destinationEndpoint) {
        Message m = new Message();
        m.setResult(result);
        m.setDestinationNodeId(destinationEndpoint.getId());
        messages.add(m);
    }

    @Override
    public void close() {

    }

    public Object getRpc() {
        return getLastMessageOrDefault().rpc;
    }

    private Message getLastMessageOrDefault() {
        return messages.isEmpty() ? new Message() : messages.getLast();
    }
}
