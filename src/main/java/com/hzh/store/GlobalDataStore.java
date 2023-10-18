package com.hzh.store;

import com.hzh.server.handler.LeaderResponseHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName GlobalDataStore
 * @Description TODO
 * @Author DaHuangGo
 * @Date 2023/10/17 19:08
 * @Version 0.0.1
 **/
public class GlobalDataStore {
    private static List<String> data = new ArrayList<>();

    public static void writeData(String message, boolean isLeader) {
        if (isLeader) {
            data.add(message);
        } else {
            // Send data to the leader for writing (you need to implement the communication)
            // After receiving confirmation from the leader, update the local data
            // Send data to the leader for writing
            String confirmation = sendDataToLeader(message);

            // After receiving confirmation from the leader, update the local data
            if ("ACK".equals(confirmation)) {
                data.add(message);
            }
        }
    }

    /**
     * 1. 使用netty建立连接
     * 2. 发送消息给leader
     * 3. 等待并接收领导的信息
     * @param message
     * @return
     */
    private static String sendDataToLeader(String message) {

        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new StringEncoder());
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new LeaderResponseHandler());
                        }
                    });

            // Connect to the leader node (replace "leaderHost" and "leaderPort" with actual values)
            Channel channel = bootstrap.connect("leaderHost", 8888).sync().channel();

            // Send the message to the leader
            channel.writeAndFlush(message);

            // Wait for the response from the leader
            LeaderResponseHandler responseHandler = channel.pipeline().get(LeaderResponseHandler.class);
            String response = responseHandler.getResponse();

            // Close the connection
            channel.close().sync();

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        } finally {
            group.shutdownGracefully();
        }
    }

    public static List<String> getData() {
        return data;
    }
}
