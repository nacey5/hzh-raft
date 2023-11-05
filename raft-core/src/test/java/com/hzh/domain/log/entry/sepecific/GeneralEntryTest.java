package com.hzh.domain.log.entry.sepecific;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GeneralEntryTest {

    private GeneralEntry generalEntryUnderTest;

    @Before
    public void setUp() {
        generalEntryUnderTest = new GeneralEntry(0, 0, 0, "content".getBytes());
    }

    @Test
    public void testToString() {
        assertEquals("GeneralEntry{index=0, term=0}", generalEntryUnderTest.toString());
    }
}
