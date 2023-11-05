package com.hzh.domain.log.sequence;

import com.hzh.domain.node.file.SeekableFile;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EntryIndexFileTest {

    @Mock
    private SeekableFile mockSeekableFile;

    private EntryIndexFile entryIndexFileUnderTest;

    @Before
    public void setUp() throws Exception {
        entryIndexFileUnderTest = new EntryIndexFile(mockSeekableFile);
    }

    @Test
    public void testAppendEntryIndex() throws IOException {
        EntryIndexItem entryIndexItem1 = new EntryIndexItem(1, 10, 0, 1);
        EntryIndexItem entryIndexItem2 = new EntryIndexItem(2, 20, 1, 2);

        entryIndexFileUnderTest.appendEntryIndex(1, 10, 0, 1);
        when(mockSeekableFile.size()).thenReturn(10L);
        entryIndexFileUnderTest.appendEntryIndex(2, 20, 1, 2);

        assertEquals(1, entryIndexFileUnderTest.getMinEntryIndex());
        assertEquals(2, entryIndexFileUnderTest.getMaxEntryIndex());

        Map<Integer, EntryIndexItem> entryIndexMap = entryIndexFileUnderTest.getEntryIndexMap();
        assertEquals(entryIndexItem1.toString(), entryIndexMap.get(1).toString());
        assertEquals(entryIndexItem2.toString(), entryIndexMap.get(2).toString());
    }


    @Test
    public void testAppendEntryIndex_SeekableFileSizeThrowsIOException() throws Exception {
        // Setup
        when(mockSeekableFile.size()).thenThrow(IOException.class);

        // Run the test
        assertThrows(IOException.class, () -> entryIndexFileUnderTest.appendEntryIndex(0, 0L, 0, 0));
    }

    @Test
    public void testAppendEntryIndex_SeekableFileWriteIntThrowsIOException() throws Exception {
        // Setup
        when(mockSeekableFile.size()).thenReturn(0L);
        doThrow(IOException.class).when(mockSeekableFile).writeInt(0);

        // Run the test
        assertThrows(IOException.class, () -> entryIndexFileUnderTest.appendEntryIndex(0, 0L, 0, 0));
    }

    @Test
    public void testRemoveAfter() throws IOException {
        entryIndexFileUnderTest.appendEntryIndex(1, 10, 0, 1);
        when(mockSeekableFile.size()).thenReturn(10L);
        entryIndexFileUnderTest.appendEntryIndex(2, 20, 1, 2);
        when(mockSeekableFile.size()).thenReturn(20L);
        entryIndexFileUnderTest.appendEntryIndex(3, 30, 2, 3);

        entryIndexFileUnderTest.removeAfter(1);

        assertEquals(1, entryIndexFileUnderTest.getMinEntryIndex());
        assertEquals(1, entryIndexFileUnderTest.getMaxEntryIndex());
        assertEquals(1, entryIndexFileUnderTest.getEntryIndexCount());
    }


    @Test
    public void testRemoveAfterWithEmptyFile() throws IOException {
        entryIndexFileUnderTest.removeAfter(1);

        assertEquals(0, entryIndexFileUnderTest.getMinEntryIndex());
        assertEquals(0, entryIndexFileUnderTest.getMaxEntryIndex());
        assertEquals(0, entryIndexFileUnderTest.getEntryIndexCount());
    }


    /**
     * max还没有设置
     * @throws IOException
     */
//    @Test
//    public void testIterator() throws IOException {
//        EntryIndexItem entryIndexItem1 = new EntryIndexItem(1, 10, 0, 1);
//        EntryIndexItem entryIndexItem2 = new EntryIndexItem(2, 20, 1, 2);
//
//        entryIndexFileUnderTest.appendEntryIndex(1, 10, 0, 1);
//        when(mockSeekableFile.size()).thenReturn(10L);
//        entryIndexFileUnderTest.appendEntryIndex(2, 20, 1, 2);
//        when(mockSeekableFile.size()).thenReturn(20L);
//
//        Iterator<EntryIndexItem> iterator = entryIndexFileUnderTest.iterator();
//
//
//        assertTrue(iterator.hasNext());
//        assertEquals(entryIndexItem1, iterator.next());
//
//        assertTrue(iterator.hasNext());
//        assertEquals(entryIndexItem2, iterator.next());
//
//        assertFalse(iterator.hasNext());
//    }

    @Test
    public void testIteratorWithEmptyFile() {
        Iterator<EntryIndexItem> iterator = entryIndexFileUnderTest.iterator();

        assertFalse(iterator.hasNext());
    }



    @Test
    public void testClear() throws Exception {
        // Setup
        // Run the test
        entryIndexFileUnderTest.clear();

        // Verify the results
        verify(mockSeekableFile).truncate(0L);
    }

    @Test
    public void testClear_SeekableFileThrowsIOException() throws Exception {
        // Setup
        doThrow(IOException.class).when(mockSeekableFile).truncate(0L);

        // Run the test
        assertThrows(IOException.class, () -> entryIndexFileUnderTest.clear());
    }


    @Test
    public void testIsEmpty() {
        assertTrue(entryIndexFileUnderTest.isEmpty());
    }
}
