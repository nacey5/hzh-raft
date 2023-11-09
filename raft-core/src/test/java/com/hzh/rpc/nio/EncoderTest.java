package com.hzh.rpc.nio;

import com.hzh.domain.message.RequestVoteRpc;
import com.hzh.domain.node.NodeId;
import com.hzh.rpc.MessageConstants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import org.junit.Test;
import raft.core.Protos;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class EncoderTest {
    @Test
    public void testNodeId() throws Exception {
        Encoder encoder=new Encoder();
        ByteBuf buffer = Unpooled.buffer();
        encoder.encode(null, NodeId.of("A"),buffer);
        assertEquals(MessageConstants.MSG_TYPE_NODE_ID,buffer.readInt());
        assertEquals(1,buffer.readInt());
        assertEquals((byte)'A',buffer.readByte());
    }

    @Test
    public void testRequestVoteRpc() throws Exception {
        Encoder encoder=new Encoder();
        ByteBuf buffer = Unpooled.buffer();
        RequestVoteRpc rpc = new RequestVoteRpc();
        rpc.setLastLogIndex(2);
        rpc.setLastLogTerm(1);
        rpc.setTerm(2);
        rpc.setCandidateId(NodeId.of("A"));
        encoder.encode(null, rpc,buffer);
        assertEquals(MessageConstants.MSG_TYPE_REQUEST_VOTE_RPC,buffer.readInt());
        buffer.readInt();//skip length
        Protos.RequestVoteRpc decodeRpc=Protos.RequestVoteRpc.parseFrom(new ByteBufInputStream(buffer));
        assertEquals(rpc.getLastLogIndex(),decodeRpc.getLastLogIndex());
        assertEquals(rpc.getLastLogTerm(),decodeRpc.getLastLogTerm());
        assertEquals(rpc.getTerm(),decodeRpc.getTerm());
        assertEquals(rpc.getCandidateId().getValue(),decodeRpc.getCandidateId());
    }
}
