package com.hzh.rpc.nio;


import com.hzh.domain.message.RequestVoteRpc;
import com.hzh.domain.node.NodeId;
import com.hzh.rpc.MessageConstants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;
import raft.core.Protos;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DecoderTest {

    @Test
    public void testNodeId() throws Exception {
        ByteBuf buffer = Unpooled.buffer();
        buffer.writeInt(MessageConstants.MSG_TYPE_NODE_ID);
        buffer.writeInt(1);
        buffer.writeByte((byte)'A');
        Decoder decoder=new Decoder();
        List<Object> out=new ArrayList<>();
        decoder.decode(null,buffer,out);
        assertEquals(NodeId.of("A"),out.get(0));
    }

    @Test
    public void testRequestVoteRpc() throws Exception {
        Protos.RequestVoteRpc rpc = Protos.RequestVoteRpc.newBuilder()
                .setLastLogTerm(1)
                .setLastLogIndex(2)
                .setTerm(2)
                .setCandidateId("A")
                .build();
        ByteBuf buffer = Unpooled.buffer();
        buffer.writeInt(MessageConstants.MSG_TYPE_REQUEST_VOTE_RPC);
        byte[] rpcBytes = rpc.toByteArray();
        buffer.writeInt(rpcBytes.length);
        buffer.writeBytes(rpcBytes);
        Decoder decoder = new Decoder();
        List<Object> out=new ArrayList<>();
        decoder.decode(null,buffer,out);
        RequestVoteRpc decodeRpc = (RequestVoteRpc) out.get(0);
        assertEquals(rpc.getLastLogIndex(),decodeRpc.getLastLogIndex());
        assertEquals(rpc.getLastLogTerm(),decodeRpc.getLastLogTerm());
        assertEquals(rpc.getTerm(),decodeRpc.getTerm());
        assertEquals(NodeId.of(rpc.getCandidateId()),decodeRpc.getCandidateId());
    }
}
