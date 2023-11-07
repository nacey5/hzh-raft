package com.hzh.domain.log;

import com.hzh.domain.log.entry.Entry;
import com.hzh.domain.log.entry.EntryMeta;
import com.hzh.domain.log.entry.sepecific.GeneralEntry;
import com.hzh.domain.log.entry.sepecific.NoOpEntry;
import com.hzh.domain.log.sequence.MemoryEntrySequence;
import com.hzh.domain.message.AppendEntriesRpc;
import com.hzh.domain.node.NodeId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static com.hzh.domain.log.AbstractLog.entrySequence;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MemoryLogTest {

    @Mock
    private MemoryEntrySequence mockEntrySequence;

    private MemoryLog memoryLogUnderTest;

    @Before
    public void setUp() throws Exception {
        memoryLogUnderTest = new MemoryLog(mockEntrySequence);
    }

    @Test
    public void testGetLastEntryMeta() {
        // Setup
        final EntryMeta expectedResult = new EntryMeta(0, 0, 0);
        when(entrySequence.getLastEntry()).thenReturn(new NoOpEntry(0, 0, 0));
        // Run the test
        final EntryMeta result = memoryLogUnderTest.getLastEntryMeta();
        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testCreateAppendEntriesRpc() {
        // Setup
        final NodeId selfId = new NodeId("value");
        final AppendEntriesRpc expectedResult = new AppendEntriesRpc();
        expectedResult.setTerm(0);
        expectedResult.setLeaderId(new NodeId("value"));
        expectedResult.setPreLogIndex(0);
        expectedResult.setPreLogTerm(0);
        expectedResult.setEntries(List.of());
        expectedResult.setLeaderCommit(0);

        // Run the test
        final AppendEntriesRpc result = memoryLogUnderTest.createAppendEntriesRpc(0, selfId, 0, 0);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testGetNextIndex() {
        assertEquals(0, memoryLogUnderTest.getNextIndex());
    }

    @Test
    public void testAppendEntry1() {
        // Setup
        // Run the test
        final NoOpEntry result = memoryLogUnderTest.appendEntry(0);

        // Verify the results
    }

    @Test
    public void testAppendEntry2() {
        // Setup
        // Run the test
        final GeneralEntry result = memoryLogUnderTest.appendEntry(0, "content".getBytes());

        // Verify the results
    }

    @Test
    public void testAppendEntriesFromLeader1() {
        // Setup
        final List<Entry> leaderEntries = List.of();

        // Run the test
        final boolean result = memoryLogUnderTest.appendEntriesFromLeader(0, 0, leaderEntries);

        // Verify the results
        assertFalse(result);
    }

    @Test
    public void testAdvanceCommitIndex() {
        // Setup
        // Run the test
        memoryLogUnderTest.advanceCommitIndex(0, 0);

        // Verify the results
    }

    @Test
    public void testClose() {
        memoryLogUnderTest.close();
    }

    @Test
    public void testCreateAppendEntriesRpcStartFromOne() {
        MemoryLog log = new MemoryLog();
        log.appendEntry(1);// 1
        log.appendEntry(1);// 2
        AppendEntriesRpc rpc = log.createAppendEntriesRpc(1, new NodeId("A"), 1, Log.ALL_ENTRIES);
        assertEquals(1, rpc.getTerm());
        assertEquals(0, rpc.getPreLogIndex());
        assertEquals(0, rpc.getPreLogTerm());
        assertEquals(2, rpc.getEntries().size());
        assertEquals(1, rpc.getEntries().get(0).getIndex());
    }

    // {index,term}
    // follower:{1,1},{2,1}
    // leader:       ,{2,1},{3,2}  
    @Test
    public void testAppendEntriesFromLeaderSkip() {
        MemoryLog log = new MemoryLog();
        log.appendEntry(1);//1
        log.appendEntry(1);//2
        List<Entry> leaderEntries = Arrays.asList(
                new NoOpEntry(2, 1),
                new NoOpEntry(3, 2)
        );
        assertTrue(log.appendEntriesFromLeader(1,1,leaderEntries));

    }
}
