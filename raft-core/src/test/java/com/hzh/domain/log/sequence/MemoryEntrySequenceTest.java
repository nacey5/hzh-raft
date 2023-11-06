package com.hzh.domain.log.sequence;

import com.hzh.domain.log.entry.Entry;
import com.hzh.domain.log.entry.EntryMeta;
import com.hzh.domain.log.entry.sepecific.NoOpEntry;
import org.junit.Before;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;


public class MemoryEntrySequenceTest {

    private MemoryEntrySequence sequence;

    @Before
    public void setUp() throws Exception {
        sequence = new MemoryEntrySequence();
    }

    @Test
    public void testAppendEntry() {
        assertEquals(1, sequence.getNextLogIndex());
        sequence.append(new NoOpEntry(sequence.getNextLogIndex(), 1));
        assertEquals(2, sequence.getNextLogIndex());
        assertEquals(1, sequence.getLastLogIndex());
    }

    @Test
    public void testGetEntry() {
        sequence = new MemoryEntrySequence(2);
        sequence.append(Arrays.asList(
                new NoOpEntry(2, 1),
                new NoOpEntry(3, 1)
        ));
        assertNull(sequence.getEntry(1));
        assertEquals(2, sequence.getEntry(2).getIndex());
        assertEquals(3, sequence.getEntry(3).getIndex());
        assertNull(sequence.getEntry(4));
    }

    @Test
    public void testGetEntryBeforeIndex() {
        sequence = new MemoryEntrySequence(2);

        ThrowingRunnable codeToTest = () -> sequence.append(Arrays.asList(
                new NoOpEntry(2, 1),
                new NoOpEntry(1, 1)
        ));
        assertThrows(IllegalArgumentException.class, codeToTest);
    }


    @Test
    public void testGetEntryMeta() {
        assertNull(sequence.getEntry(1));
        sequence.append(new NoOpEntry(1, 1));
        EntryMeta meta = sequence.getEntryMeta(1);
        assertNotNull(meta);
        assertEquals(1, meta.getIndex());
        assertEquals(1, meta.getTerm());
    }

    @Test
    public void testSubListOneElement() {
        sequence = new MemoryEntrySequence(2);
        sequence.append(Arrays.asList(
                new NoOpEntry(2, 1),
                new NoOpEntry(3, 1)
        ));
        List<Entry> subList = sequence.subList(2, 3);
        assertEquals(1, subList.size());
        assertEquals(2,subList.get(0).getIndex());
    }

    @Test
    public void testRemoveAfterPartial(){
        sequence = new MemoryEntrySequence(2);
        sequence.append(Arrays.asList(
                new NoOpEntry(2, 1),
                new NoOpEntry(3, 1)
        ));
        List<Entry> subList = sequence.subList(2, 4);
        assertEquals(2, subList.size());
        sequence.removeAfter(2);
        assertEquals(2,sequence.getLastLogIndex());
        assertEquals(3,sequence.getNextLogIndex());
        subList = sequence.subList(2, 4);
        assertEquals(1, subList.size());
        assertEquals(2,subList.get(0).getIndex());
    }


}
