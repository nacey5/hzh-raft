package com.hzh.server;

import com.hzh.server.handler.HeartbeatClientHandler;
import com.hzh.server.handler.HeartbeatServerHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName NettyServer2
 * @Description TODO
 * @Author DaHuangGo
 * @Date 2023/10/17 15:44
 * @Version 0.0.1
 **/
public class NettyServer2 {
    private NioEventLoopGroup group;
    private Channel serverChannel;
    private List<Channel> replicaChannels = new ArrayList<>();

    private boolean isLeader=false;

    public void startServer(int port) {
        group = new NioEventLoopGroup();

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

            serverChannel = serverBootstrap.bind(port).sync().channel();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    //在此时此刻，主节点充当的是客户端
    public void connectToReplica(String host, int port) {
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

        Channel replicaChannel = bootstrap.connect(host, port).syncUninterruptibly().channel();
        replicaChannels.add(replicaChannel);
    }

    // 添加一个方法来关闭所有节点
    public void closeAllNodes() {
        stopServer();
        for (Channel replicaChannel : replicaChannels) {
            if (replicaChannel != null) {
                replicaChannel.close().syncUninterruptibly();
            }
        }
    }
    private void stopServer() {
        if (serverChannel != null) {
            serverChannel.close();
        }
        if (group != null) {
            group.shutdownGracefully();
        }
    }

    public boolean isLeader(){
        return isLeader;
    }

    public void becomeLeader(){
        isLeader=true;
    }

    public void becomeFollower(){
        isLeader=false;
    }



    public static void main(String[] args) {
        // 主节点连接到副本节点
    }
}
