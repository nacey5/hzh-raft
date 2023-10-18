package com.hzh.store;

import com.hzh.server.NettyServer2;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName FollowerNode
 * @Description TODO
 * @Author DaHuangGo
 * @Date 2023/10/17 19:09
 * @Version 0.0.1
 **/
public class FollowerNode {



    private static NioEventLoopGroup workerGroup = new NioEventLoopGroup();


    private static  List<String> data=new ArrayList<>();



    public static void main(String[] args) {

        // Start the server and become a follower
        startFollowerServer();
        // Start the server and become a follower

        // Listen for data sent by the leader and update the local data
        // ...
        connectToFollowerNode("localhost", 8888);
        // Continue to run as a follower
        // ...
    }


    private static void startFollowerServer() {
        // Implement the server initialization code for followers
        // ...
        NettyServer2 replicaNode1 = new NettyServer2();
        NettyServer2 replicaNode2 = new NettyServer2();

        replicaNode1.startServer(8889);
        replicaNode2.startServer(8890);
        // Listen for messages sent by the leader
        // ...
    }

    private static void connectToFollowerNode(String host, int port) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new StringEncoder());
                        ch.pipeline().addLast(new FollowerClientHandler());
                    }
                });

        Channel followerChannel = bootstrap.connect(host, port).syncUninterruptibly().channel();
        LeaderNode.followerChannels.add(followerChannel);
    }


    private static class FollowerClientHandler extends SimpleChannelInboundHandler<String> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) {
            // Handle messages received from the leader
            System.out.println("Received message from leader: " + msg);
            data.add(msg);
            System.out.println("the follower node received msg successful");
            // Process the message and update the local data if necessary

            // Continue processing other tasks as a follower
            // ...
        }
    }
}
