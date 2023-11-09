package com.hzh.rpc.nio;

import com.hzh.domain.message.AppendEntriesResult;
import com.hzh.domain.message.AppendEntriesRpc;
import com.hzh.domain.message.RequestVoteResult;
import com.hzh.domain.message.RequestVoteRpc;

/**
 * @ClassName NioChannel
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/8 19:21
 * @Version 0.0.1
 **/
public class NioChannel implements Channel {
    private final io.netty.channel.Channel nettyChannel;

    public NioChannel(io.netty.channel.Channel nettyChannel) {
        this.nettyChannel = nettyChannel;
    }

    @Override
    public void writeRequestVoteRpc(RequestVoteRpc rpc) {
        nettyChannel.writeAndFlush(rpc);
    }

    @Override
    public void writeRequestVoteRpc(RequestVoteResult result) {
        nettyChannel.writeAndFlush(result);
    }

    @Override
    public void writeAppendEntriesRpc(AppendEntriesRpc rpc) {
        nettyChannel.writeAndFlush(rpc);
    }

    @Override
    public void writeAppendEntriesResult(AppendEntriesResult result) {
        nettyChannel.writeAndFlush(result);
    }

    @Override
    public void close() {
        try {
            nettyChannel.close().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    //Get the underlying Netty Channel
    io.netty.channel.Channel getDelegate() {
        return nettyChannel;
    }
}
