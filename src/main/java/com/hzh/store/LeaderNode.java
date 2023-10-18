package com.hzh.store;

import com.hzh.server.NettyServer2;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName LeaderNode
 * @Description TODO
 * @Author DaHuangGo
 * @Date 2023/10/17 19:09
 * @Version 0.0.1
 **/
public class LeaderNode {


    public static List<Channel> followerChannels = new ArrayList<>();
    private static NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private static NioEventLoopGroup workerGroup = new NioEventLoopGroup();


    public static void main(String[] args) {

        startLeaderServer();

        // Start the server and become the leader
        GlobalDataStore.writeData("Data to write", true);

        // Broadcast data to all followers (you need to implement this)
        // ...

        // Continue to run as the leader
        // ...
    }

    private static void startLeaderServer() {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new StringEncoder());
                        ch.pipeline().addLast(new LeaderServerHandler());
                    }
                });

        // Bind to the server port
        serverBootstrap.bind(8888).syncUninterruptibly();
    }

    private static class LeaderServerHandler extends SimpleChannelInboundHandler<String> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) {
            // Handle messages received from followers
            System.out.println("Received message from follower: " + msg);

            // Process the message and update the data if necessary

            // Broadcast the message to all followers
            broadcastToFollowers(msg);
        }
    }

    private static void broadcastToFollowers(String message) {
        for (Channel followerChannel : followerChannels) {
            if (followerChannel != null && followerChannel.isActive()) {
                // Send the message to the follower
                followerChannel.writeAndFlush(message);
            }
        }
    }
}
