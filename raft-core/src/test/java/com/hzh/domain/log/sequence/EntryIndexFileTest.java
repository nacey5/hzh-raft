package com.hzh.domain.log.sequence;

import com.hzh.domain.node.file.ByteArraySeekableFile;
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
     *
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

    private ByteArraySeekableFile makeEntryIndexFileContent(int minEntryIndex, int maxEntryIndex) throws IOException {
        ByteArraySeekableFile seekableFile = new ByteArraySeekableFile();
        seekableFile.writeInt(minEntryIndex);
        seekableFile.writeInt(maxEntryIndex);
        for (int i = minEntryIndex; i <= maxEntryIndex; i++) {
            seekableFile.writeLong(10L * i); //offset
            seekableFile.writeInt(1); //kind
            seekableFile.writeInt(i); //term
        }
        seekableFile.seek(0L);
        return seekableFile;
    }

    @Test
    public void testLoad() throws IOException {
        ByteArraySeekableFile seekableFile = makeEntryIndexFileContent(3, 4);
        EntryIndexFile file = new EntryIndexFile(seekableFile);
        assertEquals(3, file.getMinEntryIndex());
        assertEquals(4, file.getMaxEntryIndex());
        assertEquals(2, file.getEntryIndexCount());
        EntryIndexItem item = file.get(3);
        assertNotNull(item);
        assertEquals(30L, item.getOffset());
        assertEquals(1, item.getKind());
        assertEquals(3, item.getTerm());
        item = file.get(4);
        assertNotNull(item);
        assertEquals(40L, item.getOffset());
        assertEquals(1, item.getKind());
        assertEquals(4, item.getTerm());
    }

    @Test
    public void testAppendEntryIndex2() throws IOException {
        ByteArraySeekableFile seekableFile = new ByteArraySeekableFile();
        EntryIndexFile file = new EntryIndexFile(seekableFile);
        file.appendEntryIndex(10, 100l, 1, 2);
        assertEquals(1, file.getEntryIndexCount());
        assertEquals(10, file.getMinEntryIndex());
        assertEquals(10, file.getMaxEntryIndex());
        seekableFile.seek(0L);
        assertEquals(10, seekableFile.readInt()); //min entry index
        assertEquals(10, seekableFile.readInt()); // max entry index
        assertEquals(100l, seekableFile.readLong()); //offset
        assertEquals(1, seekableFile.readInt()); //kind
        assertEquals(2, seekableFile.readInt()); //term
        EntryIndexItem item = file.get(10);
        assertNotNull(item);
        assertEquals(100L, item.getOffset());
        assertEquals(1, item.getKind());
        assertEquals(2, item.getTerm());
        file.appendEntryIndex(11, 200L, 1, 2);
        assertEquals(2, file.getEntryIndexCount());
        assertEquals(10, file.getMinEntryIndex());
        assertEquals(11, file.getMaxEntryIndex());
        seekableFile.seek(24L); //skip min/max and first entry index
        assertEquals(200L, seekableFile.readLong()); //offset
        assertEquals(1, seekableFile.readInt()); //kind
        assertEquals(2, seekableFile.readInt()); //term
    }

    @Test
    public void testClear2() throws IOException {
        ByteArraySeekableFile seekableFile = makeEntryIndexFileContent(5, 6);
        EntryIndexFile file = new EntryIndexFile(seekableFile);
        assertFalse(file.isEmpty());
        file.clear();
        assertTrue(file.isEmpty());
        assertEquals(0, file.getEntryIndexCount());
        assertEquals(0L, seekableFile.size());
    }

    @Test
    public void testRemoveAfter2() throws IOException {
        ByteArraySeekableFile seekableFile = makeEntryIndexFileContent(5, 6);
        EntryIndexFile file = new EntryIndexFile(seekableFile);
        assertFalse(file.isEmpty());
        file.clear();
        assertTrue(file.isEmpty());
        assertEquals(0, file.getEntryIndexCount());
        assertEquals(0L, seekableFile.size());
    }

    @Test
    public void testGet2() throws IOException {
        EntryIndexFile file = new EntryIndexFile(makeEntryIndexFileContent(3, 4));
        EntryIndexItem item = file.get(3);
        assertNotNull(item);
        assertEquals(1, item.getKind());
        assertEquals(3, item.getTerm());
    }

    @Test
    public void testIterator() throws IOException {
        EntryIndexFile file = new EntryIndexFile(makeEntryIndexFileContent(3, 4));
        Iterator<EntryIndexItem> iterator = file.iterator();
        assertTrue(iterator.hasNext());
        EntryIndexItem item = iterator.next();
        assertEquals(3, item.getIndex());
        assertEquals(1, item.getKind());
        assertEquals(3, item.getTerm());
        assertTrue(iterator.hasNext());
        item = iterator.next();
        assertEquals(4, item.getIndex());
        assertFalse(iterator.hasNext());
    }
}
