package com.hzh.rpc;

import com.hzh.domain.message.*;
import com.hzh.domain.node.NodeBuilder;
import com.hzh.domain.node.NodeEndpoint;
import com.hzh.domain.node.NodeId;
import com.hzh.domain.node.NodeImpl;
import com.hzh.domain.role.specific.CandidateNodeRole;
import com.hzh.domain.role.specific.FollowerNodeRole;
import com.hzh.domain.role.specific.LeaderNodeRole;
import com.hzh.domain.timer.sepecific.NullScheduler;
import com.hzh.exctutor.sepecific.DirectTaskExecutor;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName NodeImplTest
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/2 14:23
 * @Version 0.0.1
 **/
public class NodeImplTest {

    private NodeBuilder newNodeBuilder(NodeId selfId, NodeEndpoint... endpoints) {
        return new NodeBuilder(Arrays.asList(endpoints), selfId)
                .setScheduler(new NullScheduler())
                .setConnector(new MockConnector())
                .setTaskExecutor(new DirectTaskExecutor());
    }

    @Test
    public void testStart() {
        NodeImpl node = (NodeImpl) newNodeBuilder(
                NodeId.of("A"), new NodeEndpoint("A", "localhost", 2333)
        ).build();
        node.start();
        FollowerNodeRole role = (FollowerNodeRole) node.getRole();
        Assert.assertEquals(0, role.getTerm());
        Assert.assertNull(role.getVotedFor());
    }


    /**
     * After the follower role is elected, it becomes the Candidate role and sends a RequestVote message to its node.
     */
    @Test
    public void testElectionTimeoutWhenFollower() {
        NodeImpl node = (NodeImpl) newNodeBuilder(
                NodeId.of("A"),
                new NodeEndpoint("A", " local host ", 2333),
                new NodeEndpoint("B", " localhost ", 2334),
                new NodeEndpoint("C", "localhost", 2335)
        ).build();
        node.start();
        node.electionTimeout();
        CandidateNodeRole role = (CandidateNodeRole) node.getRole();
        Assert.assertEquals(1, role.getTerm());
        Assert.assertEquals(1, role.getVotesCount());
        MockConnector connector = (MockConnector) node.getContext().getConnector();
        //send the RequestVote to other node
        RequestVoteRpc rpc = (RequestVoteRpc) connector.getRpc();
        Assert.assertEquals(1, rpc.getTerm());
        Assert.assertEquals(NodeId.of("A"), rpc.getCandidateId());
        Assert.assertEquals(0, rpc.getLastLogIndex());
        Assert.assertEquals(0, rpc.getLastLogTerm());
    }

    /**
     * The Follower node receives the RequestVote message from other nodes, votes and sets its own votedFor to the id of the message source node.
     */
    @Test
    public void NodeImplTest() {
        NodeImpl node = (NodeImpl) newNodeBuilder(
                NodeId.of("A"),
                new NodeEndpoint("A", " local host ", 2333),
                new NodeEndpoint("B", " localhost ", 2334),
                new NodeEndpoint("C", "localhost", 2335)
        ).build();
        node.start();
        RequestVoteRpc rpc = new RequestVoteRpc();
        rpc.setTerm(1);
        rpc.setCandidateId(NodeId.of("C"));
        rpc.setLastLogIndex(0);
        rpc.setLastLogTerm(0);
        node.onReceiveRequestVoteRpc(new RequestVoteRpcMessage(rpc,NodeId.of("C")));
        MockConnector connector = (MockConnector) node.getContext().getConnector();
        RequestVoteResult result = (RequestVoteResult) connector.getResult();
        Assert.assertEquals(1,result.getTerm());
        Assert.assertTrue(result.isVoteGranted());
        Assert.assertEquals(NodeId.of("C"),((FollowerNodeRole)node.getRole()).getVotedFor());

    }

    /**
     * Nodes A, B, and C in the 3-node system. After A becomes the Candidate role, it receives the vote.
     * The RequestVote response then becomes the leader role.
     */
    @Test
    public void testOnReceiveRequestVoteResult(){
        NodeImpl node = (NodeImpl) newNodeBuilder(
                NodeId.of("A"),
                new NodeEndpoint("A", " local host ", 2333),
                new NodeEndpoint("B", " localhost ", 2334),
                new NodeEndpoint("C", "localhost", 2335)
        ).build();
        node.start();
        node.electionTimeout();
        node.onReceiveRequestVoteResult(new RequestVoteResult(1,true));
        LeaderNodeRole role = (LeaderNodeRole) node.getRole();
        Assert.assertEquals(1,role.getTerm());
    }
    /**
     * Among the nodes A, B, and C in the 3-node system, node A becomes the leader node and sends heartbeats to B and C.
     * Jump message.
     */
    @Test
    public void testReplicateLog(){
        NodeImpl node = (NodeImpl) newNodeBuilder(
                NodeId.of("A"),
                new NodeEndpoint("A", " local host ", 2333),
                new NodeEndpoint("B", " localhost ", 2334),
                new NodeEndpoint("C", "localhost", 2335)
        ).build();
        node.start();
        //send the RequestVote message
        node.electionTimeout();
        node.onReceiveRequestVoteResult(new RequestVoteResult(1,true));
        node.replicateLog();
        MockConnector connector = (MockConnector) node.getContext().getConnector();
        //all of them are 3 message
        Assert.assertEquals(3,connector.getMessageCount());
        //check the point node
        List<MockConnector.Message> messages = connector.getMessages();
        Set<NodeId> destinationNodeIds = messages.subList(1, 3).stream().map(MockConnector.Message::getDestinationNodeId)
                .collect(Collectors.toSet());
        Assert.assertEquals(2,destinationNodeIds.size());
        Assert.assertTrue(destinationNodeIds.contains(NodeId.of("B")));
        Assert.assertTrue(destinationNodeIds.contains(NodeId.of("C")));
        AppendEntriesRpc rpc=(AppendEntriesRpc) messages.get(2).getRpc();
        Assert.assertEquals(1,rpc.getTerm());
    }

    /**
     * Nodes A, B, and C of the 3-node system. After node A starts, it receives the heartbeat from Lead node B.
     * Message, set your own term and leaderId and reply OK
     */
    @Test
    public void testOnReceiveAppendEntriesRpcFollower(){
        NodeImpl node = (NodeImpl) newNodeBuilder(
                NodeId.of("A"),
                new NodeEndpoint("A", " local host ", 2333),
                new NodeEndpoint("B", " localhost ", 2334),
                new NodeEndpoint("C", "localhost", 2335)
        ).build();
        node.start();
        AppendEntriesRpc rpc = new AppendEntriesRpc();
        rpc.setTerm(1);
        rpc.setLeaderId(NodeId.of("B"));
        node.onReceiveAppendEntriesRpc(new AppendEntriesRpcMessage(rpc,NodeId.of("B")));
        MockConnector connector = (MockConnector) node.getContext().getConnector();
        AppendEntriesResult result = (AppendEntriesResult) connector.getResult();
        Assert.assertEquals(1,result.getTerm());
        Assert.assertTrue(result.isSuccess());
        FollowerNodeRole role = (FollowerNodeRole) node.getRole();
        Assert.assertEquals(1,role.getTerm());
        Assert.assertEquals(NodeId.of("B"),role.getLeaderId());
    }

    /**
     * The nodes of the 3-node system are .A B, C China, A is the Leader node, sends messages to other nodes and receives replies.
     */

    @Test
    public void testOnReceiveAppendEntriesNormal(){
        NodeImpl node = (NodeImpl) newNodeBuilder(
                NodeId.of("A"),
                new NodeEndpoint("A", " local host ", 2333),
                new NodeEndpoint("B", " localhost ", 2334),
                new NodeEndpoint("C", "localhost", 2335)
        ).build();
        node.start();
        //become candidate
        node.electionTimeout();
        node.onReceiveRequestVoteResult(new RequestVoteResult(1,true));
        //become leader
        node.replicateLog();
        node.onReceiveAppendEntriesResult(new AppendEntriesResultMessage(
                new AppendEntriesResult("",1,true),
                NodeId.of("B"),
                new AppendEntriesRpc()
        ));
    }

}
