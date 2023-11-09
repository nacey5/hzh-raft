package com.hzh.rpc.nio;

import com.hzh.domain.message.RequestVoteRpc;
import com.hzh.domain.node.NodeId;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import raft.core.Protos;
import com.hzh.rpc.MessageConstants;

import java.util.List;

/**
 * @ClassName Decoder
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/8 16:26
 * @Version 0.0.1
 **/
public class Decoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //Read 8 bytes ahead (message type plus payload length)
        int availableBytes = in.readableBytes();
        if (availableBytes < 8) {
            return;
        }
        //record now position
        in.markReaderIndex();
        int messageType=in.readInt();
        int payloadLength=in.readInt();
        //The message is not yet fully readable (half-packed state)
        if(in.readableBytes() < payloadLength){
            // back to the origin position
            in.resetReaderIndex();
            return;
        }
        //the message can read
        byte[] payload=new byte[payloadLength];
        in.readBytes(payload);
        //Deserialize based on message type
        switch (messageType){
            case MessageConstants.MSG_TYPE_NODE_ID:
                out.add(new NodeId(new String(payload)));
                break;
            case MessageConstants.MSG_TYPE_REQUEST_VOTE_RPC:
                Protos.RequestVoteRpc protoRVRpc = Protos.RequestVoteRpc.parseFrom(payload);
                RequestVoteRpc rpc = new RequestVoteRpc();
                rpc.setTerm(protoRVRpc.getTerm());
                rpc.setCandidateId(new NodeId(protoRVRpc.getCandidateId()));
                rpc.setLastLogIndex(protoRVRpc.getLastLogIndex());
                rpc.setLastLogTerm(protoRVRpc.getLastLogTerm());
                out.add(rpc);
                break;
        }
    }
}
