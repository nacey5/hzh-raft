package com.hzh.server;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


public class NettyServerTest {

    private NettyServer server;

    @Before
    public void setUp() {
        server = new NettyServer();
        // 在测试前启动服务器
        server.startServer();

        // 使用CountDownLatch等待服务器启动
        waitUntilServerIsRunning(server);
    }

    @After
    public void tearDown() {
        // 在测试后停止服务器
        server.stopServer();
    }

    @Test
    public void testServer() {
        // 编写你的测试逻辑
        // 这里可以使用Netty的客户端来模拟发送消息并验证服务器的响应
        Assert.assertTrue(server.isRunning());
    }


    private void waitUntilServerIsRunning(NettyServer server) {
        int timeoutSeconds = 10;
        CountDownLatch latch = new CountDownLatch(1);

        Thread waiterThread = new Thread(() -> {
            while (!server.isRunning()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            latch.countDown();
        });

        waiterThread.start();

        try {
            if (!latch.await(timeoutSeconds, TimeUnit.SECONDS)) {
                throw new RuntimeException("Server did not start within " + timeoutSeconds + " seconds.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
