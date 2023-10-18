package com.hzh.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName HeartbeatClient
 * @Description TODO
 * @Author DaHuangGo
 * @Date 2023/10/17 13:03
 * @Version 0.0.1
 **/
public class HeartbeatClient {

    NioEventLoopGroup group = new NioEventLoopGroup();

    private Channel channel;
    private String responseMessage;

    public static void main(String[] args) throws InterruptedException {
        HeartbeatClient heartbeatClient = new HeartbeatClient();
        heartbeatClient.startClient();
        Thread.sleep(5000);
        heartbeatClient.sendHeartbeat("Heartbeat");
    }


    public void startClient() {

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new StringEncoder());
                            ch.pipeline().addLast(new HeartbeatClientHandler());
                        }
                    });

            ChannelFuture future=bootstrap.connect("localhost", 8889).sync().channel().closeFuture().sync();
            channel=future.channel();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            stopClient();
        }
    }


    public void stopClient() {
        if (channel != null) {
            channel.close();
        }
        if (group != null) {
            group.shutdownGracefully();
        }
    }

    public String waitForResponse() {
        return responseMessage;
    }



    private static class HeartbeatClientHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws InterruptedException {
//             连接建立后，发送心跳消息
//             连接建立后，添加一个定时任务，定期发送心跳消息
            ctx.executor().scheduleAtFixedRate(() -> {
                ctx.writeAndFlush("Heartbeat\n");
            }, 0, 5, TimeUnit.SECONDS);
        }

//        @Override


//        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//            super.channelRead(ctx, msg);
//        }
    }

    public boolean sendHeartbeat(String message) {
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(message);
            return true;
        }
        return false;
    }


}

