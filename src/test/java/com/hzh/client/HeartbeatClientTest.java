package com.hzh.client;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HeartbeatClientTest {

    private HeartbeatClient heartbeatClient;

    @Before
    public void setUp() {
        heartbeatClient = new HeartbeatClient();
        heartbeatClient.startClient();
    }

    @After
    public void tearDown(){
        heartbeatClient.stopClient();
    }

    @Test
    public void testClient(){
        // 发送心跳消息
        boolean heartbeatSent = heartbeatClient.sendHeartbeat("Heartbeat");
        assertTrue(heartbeatSent);

        // 等待服务器响应
        String response = heartbeatClient.waitForResponse();
        assertEquals("Received heartbeat from client.", response);
    }
}
