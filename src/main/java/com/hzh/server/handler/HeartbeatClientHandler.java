package com.hzh.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName HeartbeatClientHandler
 * @Description TODO
 * @Author DaHuangGo
 * @Date 2023/10/17 17:01
 * @Version 0.0.1
 **/
public class HeartbeatClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String message = (String) msg;
        if ("Heartbeat".equals(message)) {
            // 收到心跳消息，可以进行相应的处理
            System.out.println("Received heartbeat from server.");
        } else {
            // 处理其他消息
            System.out.println("Received message: " + message);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.executor().scheduleAtFixedRate(() -> {
            ctx.writeAndFlush("Heartbeat\n");
        }, 0, 5, TimeUnit.SECONDS);
    }
}
