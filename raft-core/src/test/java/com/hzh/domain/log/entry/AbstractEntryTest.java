package com.hzh.domain.log.entry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class AbstractEntryTest {

    private AbstractEntry entry;

    @Before
    public void setUp() {
        entry = new AbstractEntry(0, 10, 100);
    }

    @Test
    public void testGetKind() {
        assertEquals(0, entry.getKind());
    }

    @Test
    public void testGetIndex() {
        assertEquals(10, entry.getIndex());
    }

    @Test
    public void testGetTerm() {
        assertEquals(100, entry.getTerm());
    }

    @Test
    public void testGetMeta() {
        EntryMeta meta = entry.getMeta();
        assertNotNull(meta);
//        assertEquals(1, meta.getKind());
//        assertEquals(2, meta.getIndex());
//        assertEquals(3, meta.getTerm());
    }

    @Test
    public void testGetCommandBytes() {
        byte[] commandBytes = entry.getCommandBytes();
        assertNotNull(commandBytes);
        assertEquals(0, commandBytes.length);
    }
}
