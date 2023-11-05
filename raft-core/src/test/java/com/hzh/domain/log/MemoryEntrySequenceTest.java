package com.hzh.domain.log;

import com.hzh.domain.log.entry.Entry;
import com.hzh.domain.log.sequence.MemoryEntrySequence;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MemoryEntrySequenceTest {

    private MemoryEntrySequence entrySequence;

    @Before
    public void setUp() {
        entrySequence = new MemoryEntrySequence(1);
    }

    @Test
    public void testGetCommitIndex() {
        assertEquals(0, entrySequence.getCommitIndex());
        entrySequence.commit(5);
        assertEquals(5, entrySequence.getCommitIndex());
    }

    @Test
    public void testDoAppend() {
        Entry entry = mock(Entry.class);
        when(entry.getIndex()).thenReturn(1);
        entrySequence.append(entry);
        Entry retrievedEntry = entrySequence.getEntry(1);
        assertNotNull(retrievedEntry);
        assertEquals(entry, retrievedEntry);
    }

    @Test
    public void testDoRemoveAfter() {
        Entry entry1 = mock(Entry.class);
        Entry entry2 = mock(Entry.class);
        Entry entry3 = mock(Entry.class);
        when(entry1.getIndex()).thenReturn(1);
        when(entry2.getIndex()).thenReturn(2);
        when(entry3.getIndex()).thenReturn(3);

        entrySequence.append(entry1);
        entrySequence.append(entry2);
        entrySequence.append(entry3);

        entrySequence.removeAfter(2);
        assertEquals(3, entrySequence.getNextLogIndex());

        assertNull(entrySequence.getEntry(3));
    }

    @Test
    public void testDoRemoveAfterWithIndexLessThanFirstLogIndex() {
        Entry entry1 = mock(Entry.class);
        Entry entry2 = mock(Entry.class);
        when(entry1.getIndex()).thenReturn(1);
        when(entry2.getIndex()).thenReturn(2);
        entrySequence.append(entry1);
        entrySequence.append(entry2);

        entrySequence.removeAfter(1);
        assertEquals(2, entrySequence.getNextLogIndex());
        assertNull(entrySequence.getEntry(0));
        assertNotNull(entrySequence.getEntry(1));
    }

    @Test
    public void testDoRemoveAfterWithEmptySequence() {
        entrySequence.removeAfter(1); // Should not throw an exception
    }

    @Test
    public void testDoSubList() {
        Entry entry1 = mock(Entry.class);
        Entry entry2 = mock(Entry.class);
        Entry entry3 = mock(Entry.class);
        when(entry1.getIndex()).thenReturn(1);
        when(entry2.getIndex()).thenReturn(2);
        when(entry3.getIndex()).thenReturn(3);

        entrySequence.append(entry1);
        entrySequence.append(entry2);
        entrySequence.append(entry3);

        List<Entry> subList = entrySequence.subList(2, 4);
        assertNotNull(subList);
        assertEquals(2, subList.size());
        assertTrue(subList.contains(entry2));
        assertTrue(subList.contains(entry3));
    }

    @Test
    public void testToString() {
        String expectedString = "MemoryEntrySequence{logIndexOffset=1, nextLogIndex=1,entries.size=0}";
        assertEquals(expectedString, entrySequence.toString());
    }

    @Test
    public void testToStringWithAppendedEntries() {
        Entry entry1 = mock(Entry.class);
        Entry entry2 = mock(Entry.class);
        when(entry1.getIndex()).thenReturn(1);
        when(entry2.getIndex()).thenReturn(2);

        entrySequence.append(entry1);
        entrySequence.append(entry2);

        String expectedString = "MemoryEntrySequence{logIndexOffset=1, nextLogIndex=3,entries.size=2}";
        assertEquals(expectedString, entrySequence.toString());
    }
}
