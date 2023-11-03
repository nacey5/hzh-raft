package com.hzh.domain.node;

import com.hzh.config.NodeConfig;
import com.hzh.domain.timer.Scheduler;
import com.hzh.exctutor.TaskExecutor;
import com.hzh.rpc.Connector;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NodeBuilderTest {

    @Mock
    private Scheduler mockScheduler;
    @Mock
    private Connector mockConnector;
    @Mock
    private TaskExecutor mockTaskExecutor;
    @Mock
    private NodeConfig mockConfig;

    private NodeBuilder nodeBuilderUnderTest;

    @Before
    public void setUp() {
        nodeBuilderUnderTest = new NodeBuilder(List.of(new NodeEndpoint("id", "host", 0)), NodeId.of("common"));
        nodeBuilderUnderTest.setScheduler(mockScheduler);
        nodeBuilderUnderTest.setConnector(mockConnector);
        nodeBuilderUnderTest.setTaskExecutor(mockTaskExecutor);
        nodeBuilderUnderTest.setConfig(mockConfig);
    }

    @Test
    public void testBuild() {
        // Setup
        // Run the test
        final Node result = nodeBuilderUnderTest.build();

        // Verify the results
        Assert.assertNotNull(result);
    }
}
