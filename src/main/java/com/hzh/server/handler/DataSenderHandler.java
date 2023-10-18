package com.hzh.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @ClassName DataSenderHandler
 * @Description TODO
 * @Author DaHuangGo
 * @Date 2023/10/18 12:02
 * @Version 0.0.1
 **/
public class DataSenderHandler extends ChannelInboundHandlerAdapter {
    private String message;
    private static String confirmation;

    public DataSenderHandler(String message) {
        this.message = message;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(message);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        confirmation = (String) msg;
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    public static String getConfirmation() {
        return confirmation;
    }
}
