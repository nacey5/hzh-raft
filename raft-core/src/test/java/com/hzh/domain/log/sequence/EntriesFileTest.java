package com.hzh.domain.log.sequence;

import com.hzh.domain.log.entry.Entry;
import com.hzh.domain.log.entry.sepecific.GeneralEntry;
import com.hzh.domain.node.file.SeekableFile;
import com.hzh.exception.EmptySequenceException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EntriesFileTest {

    @Mock
    private SeekableFile mockSeekableFile;

    private EntriesFile entriesFileUnderTest;

    private File tempFile;

    @Before
    public void setUp() throws Exception {
        tempFile = File.createTempFile("test-entries-file", null);
        entriesFileUnderTest = new EntriesFile(tempFile);
    }

    @Test
    public void testAppendEntryAndLoadEntry() throws Exception {
        // Setup
        EntryFactory entryFactory = new EntryFactory();
        Entry entry1 = new GeneralEntry(1, 2, new byte[]{1, 2, 3});
        Entry entry2 = new GeneralEntry(2, 3, new byte[]{4, 5, 6});

        long offset1 = entriesFileUnderTest.appendEntry(entry1);
        long offset2 = entriesFileUnderTest.appendEntry(entry2);

        assertEquals(0, offset1);
        assertEquals(19, offset2);

        Entry loadedEntry1 = entriesFileUnderTest.loadEntry(offset1, entryFactory);
        Entry loadedEntry2 = entriesFileUnderTest.loadEntry(offset2, entryFactory);
        //使用同toString()是因为在这个过程当中，工厂组装是一个新的对象
        assertEquals(entry1.toString(), loadedEntry1.toString());
        assertEquals(entry2.toString(), loadedEntry2.toString());
    }

    @Test
    public void testAppendEntry_SeekableFileSizeThrowsIOException() throws Exception {
        // Setup
        final Entry entry = null;
        // Run the test
        assertThrows(EmptySequenceException.class, () -> entriesFileUnderTest.appendEntry(entry));
    }
    @Test
    public void testLoadEntry_SeekableFileSizeThrowsIOException() throws Exception {
        // Setup
        final EntryFactory factory = new EntryFactory();
        // Run the test
        assertThrows(IOException.class, () -> entriesFileUnderTest.loadEntry(0L, factory));
    }

    @Test
    public void testLoadEntry_SeekableFileSeekThrowsIOException() throws Exception {
        // Setup
        final EntryFactory factory = new EntryFactory();

        // Run the test
        assertThrows(IOException.class, () -> entriesFileUnderTest.loadEntry(0L, factory));
    }


    @Test
    public void testSize() throws Exception {
        Entry entry = new GeneralEntry(1, 2, new byte[]{1, 2, 3});
        long offset = entriesFileUnderTest.appendEntry(entry);
        assertEquals(19, entriesFileUnderTest.size());

        EntryFactory entryFactory = new EntryFactory();
        Entry loadedEntry = entriesFileUnderTest.loadEntry(offset, entryFactory);
        assertEquals(entry.toString(), loadedEntry.toString());
    }

    @Test
    public void testClear() throws IOException {
        Entry entry = new GeneralEntry(1, 2, new byte[]{1, 2, 3});
        entriesFileUnderTest.appendEntry(entry);

        assertEquals(19, entriesFileUnderTest.size());

        entriesFileUnderTest.clear();
        assertEquals(0, entriesFileUnderTest.size());
    }

    @Test
    public void testClose() throws IOException {
        SeekableFile seekableFile = mock(SeekableFile.class);
        EntriesFile entriesFile = new EntriesFile(seekableFile);
        entriesFile.close();

        verify(seekableFile).close();
    }

}
