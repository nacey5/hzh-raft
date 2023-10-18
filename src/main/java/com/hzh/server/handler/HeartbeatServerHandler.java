package com.hzh.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
  *@ClassName HeartbeatServerHandler
  *@Description TODO
  *@Author DaHuangGo
  *@Date 2023/10/17 17:00
  *@Version 0.0.1
  **/
public class HeartbeatServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String message = (String) msg;
        if ("Heartbeat".equals(message)) {
            // 收到心跳消息，可以进行相应的处理
            System.out.println("Received heartbeat from client.");
            // 向客户端发送响应消息
            ctx.writeAndFlush("Heartbeat Response\n");
        } else {
            // 处理其他消息
            System.out.println("Received message: " + message);
        }
    }


}
