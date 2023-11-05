package com.hzh.domain.log.entry.sepecific;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class NoOpEntryTest {

    private NoOpEntry noOpEntryUnderTest;

    @Before
    public void setUp() {
        noOpEntryUnderTest = new NoOpEntry(0, 0, 0);
    }

    @Test
    public void testGetCommandBytes() {
        assertArrayEquals(new byte[0], noOpEntryUnderTest.getCommandBytes());
    }

    @Test
    public void testToString() {
        assertEquals("NoOpEntry{index=0,term=0}", noOpEntryUnderTest.toString());
    }
}
