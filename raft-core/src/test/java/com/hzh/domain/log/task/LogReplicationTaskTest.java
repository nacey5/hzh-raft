package com.hzh.domain.log.task;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LogReplicationTaskTest {

    @Mock
    private ScheduledFuture<?> mockScheduledFuture;

    private LogReplicationTask logReplicationTaskUnderTest;

    @Before
    public void setUp() {
        logReplicationTaskUnderTest = new LogReplicationTask(mockScheduledFuture);
    }

    @Test
    public void testCancel() {
        // Setup
        // Run the test
        logReplicationTaskUnderTest.cancel();

        // Verify the results
        verify(mockScheduledFuture).cancel(false);
    }

    @Test
    public void testToString() {
        // Setup
        when(mockScheduledFuture.getDelay(TimeUnit.MILLISECONDS)).thenReturn(0L);

        // Run the test
        final String result = logReplicationTaskUnderTest.toString();

        // Verify the results
        assertEquals(" LogReplicationTask{delay- 0 }", result);
    }
}
