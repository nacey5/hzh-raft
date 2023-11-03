package com.hzh.domain.node;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NodeGroupTest {

    @Mock
    private NodeId mockSelfId;

    private NodeGroup nodeGroupUnderTest;

    @Before
    public void setUp() {
        nodeGroupUnderTest = new NodeGroup(List.of(new NodeEndpoint("id", "host", 0)), mockSelfId);
    }

    @Test
    public void testListEndpointExceptSelf() {
        // Setup
        final Collection<NodeEndpoint> expectedResult = List.of(new NodeEndpoint("id", "host", 0));

        // Run the test
        final Collection<NodeEndpoint> result = nodeGroupUnderTest.listEndpointExceptSelf();

        // Verify the results
        assertEquals(expectedResult.size(), result.size());
    }

    @Test
    public void testGetMember() {
        // Setup
        final NodeId id = new NodeId("id");
        final GroupMember expectedResult = new GroupMember(new NodeEndpoint("id", "host", 0), null);

        // Run the test
        final GroupMember result = nodeGroupUnderTest.getMember(id);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testGetCount() {
        // Setup
        // Run the test
        final int result = nodeGroupUnderTest.getCount();

        // Verify the results
        assertEquals(1, result);
    }

    @Test
    public void testListReplicationTargets() {
        // Setup
        final Collection<GroupMember> expectedResult = List.of(
                new GroupMember(new NodeEndpoint("id", "host", 0), null));

        // Run the test
        final Collection<GroupMember> result = nodeGroupUnderTest.listReplicationTargets();

        // Verify the results
        assertEquals(expectedResult, result);
    }
}
