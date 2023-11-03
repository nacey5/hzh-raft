package com.hzh.domain.node.memory;

import com.hzh.domain.node.NodeId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MemoryNodeStoreTest {

    @Mock
    private NodeId mockVotedFor;

    private MemoryNodeStore memoryNodeStoreUnderTest;

    @Before
    public void setUp() {
        memoryNodeStoreUnderTest = new MemoryNodeStore(0, mockVotedFor);
    }

    @Test
    public void testClose() {
        memoryNodeStoreUnderTest.close();
    }
}
