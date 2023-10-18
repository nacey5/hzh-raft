package com.hzh.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName NettyServer
 * @Description TODO
 * @Author DaHuangGo
 * @Date 2023/10/17 12:56
 * @Version 0.0.1
 **/
public class NettyServer {


    public static final HashSet<Integer> idSet=new HashSet<>();

    NioEventLoopGroup group = new NioEventLoopGroup();
    private boolean isRuning;

    //预留字段，后续的选举以及转发通过这个进行
    private  int id;




    public static void main(String[] args) throws InterruptedException {
        new NettyServer().startServer();
        // Start server and client here
    }

    public NettyServer() {
        this.id = generateUniqueId();
    }

    public void startServer() {
        group = new NioEventLoopGroup();

        Thread serverThread = new Thread(() -> {
            try {
                ServerBootstrap serverBootstrap = new ServerBootstrap();
                serverBootstrap.group(group)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) {
                                ch.pipeline().addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
                                ch.pipeline().addLast(new StringDecoder());
                                ch.pipeline().addLast(new StringEncoder());
                                ch.pipeline().addLast(new HeartbeatServerHandler());
                            }
                        });
                isRuning=true;
                serverBootstrap.bind(8888).sync().channel().closeFuture().sync();
            } catch (InterruptedException e) {
                isRuning=false;
                throw new RuntimeException(e);
            } finally {
                stopServer();
            }
        });

        serverThread.start();
        // 启动服务器的代码
    }

    public void stopServer() {
        if (group!=null){
            group.shutdownGracefully();
            // 停止服务器的代码
        }
    }

    public boolean isRunning(){
        return isRuning;
    }

    private int generateUniqueId() {
        AtomicInteger uniqueId = new AtomicInteger(0);
        int id;
        do {
            id = uniqueId.getAndIncrement();
        } while (idSet.contains(id));
        idSet.add(id);
        return id;
    }

    public Integer getId(){
        return this.id;
    }

    private static class HeartbeatServerHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            String message = (String) msg;
            if ("Heartbeat".equals(message)) {
                // 收到心跳消息，可以进行相应的处理
                System.out.println("Received heartbeat from client.");
            } else {
                // 处理其他消息
                System.out.println("Received message: " + message);
            }
        }
    }



}
