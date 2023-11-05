package com.hzh.domain.log;

import com.hzh.domain.log.entry.Entry;
import com.hzh.domain.log.entry.EntryMeta;
import com.hzh.domain.log.sequence.AbstractEntrySequence;
import com.hzh.exception.EmptySequenceException;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbstractEntrySequenceTest {

    private AbstractEntrySequence abstractEntrySequenceUnderTest;
    private List<Entry> entryList=new LinkedList<>();

    @Before
    public void setUp() throws Exception {
        abstractEntrySequenceUnderTest = new AbstractEntrySequence(0) {


            @Override
            protected List<Entry> doSubList(int fromIndex, int toIndex) {
                return entryList.stream()
                        .filter(entry -> entry.getIndex() >= fromIndex && entry.getIndex() < toIndex)
                        .collect(Collectors.toList());
            }

            @Override
            public Entry doGetEntry(int index) {
                return entryList.get(index);
            }


            @Override
            protected void doAppend(Entry entry) {
                entryList.add(entry);
            }


            @Override
            public void commit(int index) {

            }

            @Override
            public int getCommitIndex() {
                return 0;
            }

            @Override
            protected void doRemoveAfter(int index) {
                entryList.remove(index+1);
                int nextLogIndex = abstractEntrySequenceUnderTest.getNextLogIndex();
                abstractEntrySequenceUnderTest.setNextLogIndex((--nextLogIndex));
            }

            @Override
            public void close() {

            }
        };
    }

    @Test
    public void testIsEmpty() {
        assertTrue(abstractEntrySequenceUnderTest.isEmpty());
    }

    @Test
    public void testGetFirstLogIndex() {
        assertThrows(EmptySequenceException.class,() -> {
            abstractEntrySequenceUnderTest.getFirstLogIndex();
        });

    }

    @Test
    public void testGetLastLogIndex() {
        assertThrows(EmptySequenceException.class,() -> {
            abstractEntrySequenceUnderTest.getLastLogIndex();
        });
    }

    @Test
    public void testIsEntryPresent() {
        assertFalse(abstractEntrySequenceUnderTest.isEntryPresent(0));
    }

    @Test
    public void testGetEntry() {
        assertNull(abstractEntrySequenceUnderTest.getEntry(0));

        Entry entry = mock(Entry.class);
        abstractEntrySequenceUnderTest.append(entry);
        assertNotNull(abstractEntrySequenceUnderTest.getEntry(0));
    }

    @Test
    public void testGetEntryMeta() {
        assertNull(abstractEntrySequenceUnderTest.getEntryMeta(0));

        Entry entry = mock(Entry.class);
        EntryMeta meta = mock(EntryMeta.class);
        when(entry.getMeta()).thenReturn(meta);
        abstractEntrySequenceUnderTest.append(entry);
        assertNotNull(abstractEntrySequenceUnderTest.getEntryMeta(0));
    }

    @Test
    public void testGetLastEntry() {
        assertNull(abstractEntrySequenceUnderTest.getLastEntry());

        Entry entry = mock(Entry.class);
        abstractEntrySequenceUnderTest.append(entry);
        assertNotNull(abstractEntrySequenceUnderTest.getLastEntry());
    }

    @Test
    public void testSubList() {
        List<Entry> subList = abstractEntrySequenceUnderTest.subList(1);
        assertNotNull(subList);
        assertTrue(subList.isEmpty());

        Entry entry = mock(Entry.class);
        abstractEntrySequenceUnderTest.append(entry);

        subList = abstractEntrySequenceUnderTest.subList(0);
        assertNotNull(subList);
        assertEquals(1, subList.size());

        subList = abstractEntrySequenceUnderTest.subList(1);
        assertNotNull(subList);
        assertTrue(subList.isEmpty());
    }

    @Test
    public void testSubListWithRange() {
        assertThrows(EmptySequenceException.class, () -> abstractEntrySequenceUnderTest.subList(1, 2));

        Entry entry = mock(Entry.class);
        abstractEntrySequenceUnderTest.append(entry);

        assertThrows(IllegalArgumentException.class, () -> abstractEntrySequenceUnderTest.subList(1, 0));

        List<Entry> subList = abstractEntrySequenceUnderTest.subList(0, 1);
        assertNotNull(subList);
        assertEquals(1, subList.size());
    }

    @Test
    public void testAppend() {
        Entry entry1 = mock(Entry.class);
        Entry entry2 = mock(Entry.class);

        abstractEntrySequenceUnderTest.append(entry1);
        assertEquals(1, abstractEntrySequenceUnderTest.getNextLogIndex());

        assertThrows(IllegalArgumentException.class, () -> abstractEntrySequenceUnderTest.append(entry2));
        assertEquals(1, abstractEntrySequenceUnderTest.getNextLogIndex());

    }

    @Test
    public void testRemoveAfter() {
        Entry entry1 = mock(Entry.class);
        Entry entry2 = mock(Entry.class);
        when(entry2.getIndex()).thenReturn(1);

        abstractEntrySequenceUnderTest.append(entry1);
        abstractEntrySequenceUnderTest.append(entry2);
        assertEquals(2, abstractEntrySequenceUnderTest.getNextLogIndex());

        abstractEntrySequenceUnderTest.removeAfter(0);
        assertEquals(1, abstractEntrySequenceUnderTest.getNextLogIndex());
    }
}
