package com.hzh.server.handler;

import com.hzh.store.GlobalDataStore;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @ClassName LeaderResponseHandler
 * @Description TODO
 * @Author DaHuangGo
 * @Date 2023/10/18 12:10
 * @Version 0.0.1
 **/
public class LeaderResponseHandler extends SimpleChannelInboundHandler<String> {
    private String response;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        // Handle the response received from the leader
        GlobalDataStore.writeData(msg,true);
        this.response = "ACK";
    }

    public String getResponse() {
        return response;
    }

}
