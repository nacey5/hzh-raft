package com.hzh.domain.timer.sepecific;

import com.hzh.config.NodeConfig;
import com.hzh.domain.log.task.LogReplicationTask;
import com.hzh.domain.timer.ElectionTimeout;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DefaultSchedulerTest {

    private DefaultScheduler defaultSchedulerUnderTest;

    @Before
    public void setUp() {
        defaultSchedulerUnderTest = new DefaultScheduler(new NodeConfig());
    }

    @Test
    public void testScheduleLogReplicationTask() {
        // Setup
        final Runnable task = ()->{};

        // Run the test
        final LogReplicationTask result = defaultSchedulerUnderTest.scheduleLogReplicationTask(task);

        // Verify the results
        Assert.assertNotNull(result);
    }

    @Test
    public void testScheduleElectionTimeout() {
        // Setup
        final Runnable task =  ()->{
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };


        // Run the test
        final ElectionTimeout result = defaultSchedulerUnderTest.scheduleElectionTimeout(task);

        // Verify the results
        // Verify the results
        Assert.assertNotNull(result);
    }

    @Test
    public void testStop() throws Exception {
        defaultSchedulerUnderTest.stop();
    }
}
