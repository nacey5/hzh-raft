package com.hzh.domain.log.sequence;

import com.hzh.domain.log.entry.Entry;
import com.hzh.domain.log.entry.sepecific.GeneralEntry;
import com.hzh.domain.log.entry.sepecific.NoOpEntry;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EntryFactoryTest {

    private EntryFactory entryFactory;

    @Before
    public void setUp() {
        entryFactory = new EntryFactory();
    }

    @Test
    public void testCreateNoOpEntry() {
        int kind = Entry.KIND_OP_OP;
        int index = 1;
        int term = 2;
        byte[] commandBytes = new byte[0];

        Entry entry = entryFactory.create(kind, index, term, commandBytes);

        assertTrue(entry instanceof NoOpEntry);
        assertEquals(index, entry.getIndex());
        assertEquals(term, entry.getTerm());
    }

    @Test
    public void testCreateGeneralEntry() {
        int kind = Entry.KIND_GENERAL;
        int index = 1;
        int term = 2;
        byte[] commandBytes = new byte[]{1, 2, 3};

        Entry entry = entryFactory.create(kind, index, term, commandBytes);

        assertTrue(entry instanceof GeneralEntry);
        assertEquals(index, entry.getIndex());
        assertEquals(term, entry.getTerm());
        assertArrayEquals(commandBytes, ((GeneralEntry) entry).getCommandBytes());
    }

    @Test
    public void testCreateInvalidKind() {
        int kind = 999; // Invalid kind

        assertThrows(IllegalArgumentException.class, () -> entryFactory.create(kind, 1, 2, new byte[0]));
    }
}
