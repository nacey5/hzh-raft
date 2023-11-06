package com.hzh.domain.log;

import com.hzh.domain.log.entry.Entry;
import com.hzh.domain.log.entry.EntryMeta;
import com.hzh.domain.log.entry.sepecific.GeneralEntry;
import com.hzh.domain.log.sequence.EntriesFile;
import com.hzh.domain.log.sequence.EntryFactory;
import com.hzh.domain.log.sequence.EntryIndexFile;
import com.hzh.domain.log.sequence.EntryIndexItem;
import com.hzh.exception.LogException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.hzh.domain.log.entry.Entry.KIND_GENERAL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FileEntrySequenceTest {

    private FileEntrySequence fileEntrySequence;
    private LogDir logDir;
    private File tempDir;
    private EntryFactory entryFactory = new EntryFactory();

    @Before
    public void setUp() throws Exception {
        tempDir = new File(System.getProperty("java.io.tmpdir"), "test-log-dir");
        tempDir.mkdirs();
        logDir = new LogDir() {
            @Override
            public void initialize() {

            }

            @Override
            public boolean exists() {
                return false;
            }

            @Override
            public File getEntriesFile() {
                return null;
            }

            @Override
            public File getEntryOffsetIndexFile() {
                return null;
            }

            @Override
            public File get() {
                return null;
            }

            @Override
            public boolean renameTo(LogDir logDir) {
                return false;
            }
        };
        fileEntrySequence = new FileEntrySequence(logDir, 1);
    }

    @Test
    public void testDoAppend() {
        GeneralEntry entry1 = new GeneralEntry(1, 1, new byte[0]);
        GeneralEntry entry2 = new GeneralEntry(2, 2, new byte[0]);
        Entry entry3 = entryFactory.create(KIND_GENERAL, 3, 3, new byte[0]);
        fileEntrySequence.append(entry1);
        fileEntrySequence.append(entry2);
        fileEntrySequence.append(entry3);

        List<Entry> entries = fileEntrySequence.subList(1);
        assertEquals(3, entries.size());
        assertEquals(entry1, entries.get(0));
        assertEquals(entry2, entries.get(1));
    }

    @Test
    public void testDoRemoveAfter() {
        GeneralEntry entry1 = new GeneralEntry(1, 1, new byte[0]);
        GeneralEntry entry2 = new GeneralEntry(2, 2, new byte[0]);
        GeneralEntry entry3 = new GeneralEntry(3, 3, new byte[0]);

        fileEntrySequence.append(entry1);
        fileEntrySequence.append(entry2);
        fileEntrySequence.append(entry3);

        fileEntrySequence.removeAfter(2);

        List<Entry> entries = fileEntrySequence.subList(1);
        assertEquals(2, entries.size());
        assertEquals(entry1, entries.get(0));
        assertEquals(entry2, entries.get(1));
    }

    @Test
    public void testCommit() {
        GeneralEntry entry1 = new GeneralEntry(1, 1, new byte[0]);
        GeneralEntry entry2 = new GeneralEntry(2, 2, new byte[0]);
        GeneralEntry entry3 = new GeneralEntry(3, 3, new byte[0]);

        fileEntrySequence.append(entry1);
        fileEntrySequence.append(entry2);
        fileEntrySequence.append(entry3);

        fileEntrySequence.commit(2);

        assertEquals(2, fileEntrySequence.getCommitIndex());
    }

    @Test
    public void testGetEntryMeta() {
        GeneralEntry entry1 = new GeneralEntry(1, 1, new byte[0]);
        GeneralEntry entry2 = new GeneralEntry(2, 2, new byte[0]);
        GeneralEntry entry3 = new GeneralEntry(3, 3, new byte[0]);

        fileEntrySequence.append(entry1);
        fileEntrySequence.append(entry2);
        fileEntrySequence.append(entry3);

        EntryMeta meta = fileEntrySequence.getEntryMeta(2);

        assertEquals(2, meta.getIndex());
        assertEquals(2, meta.getTerm());
    }

    @Test
    public void testGetLastEntry() {
        GeneralEntry entry1 = new GeneralEntry(1, 1, new byte[0]);
        GeneralEntry entry2 = new GeneralEntry(2, 2, new byte[0]);
        GeneralEntry entry3 = new GeneralEntry(3, 3, new byte[0]);

        fileEntrySequence.append(entry1);
        fileEntrySequence.append(entry2);
        fileEntrySequence.append(entry3);

        GeneralEntry lastEntry = (GeneralEntry)fileEntrySequence.getLastEntry();

        assertEquals(entry3, lastEntry);
    }

    @Test
    public void testGetLastEntryWithPendingEntries() {
        GeneralEntry entry1 = new GeneralEntry(1, 1, new byte[0]);
        GeneralEntry entry2 = new GeneralEntry(2, 2, new byte[0]);
        GeneralEntry entry3 = new GeneralEntry(3, 3, new byte[0]);

        fileEntrySequence.append(entry1);
        fileEntrySequence.append(entry2);
        fileEntrySequence.append(entry3);

        fileEntrySequence.removeAfter(2);

        GeneralEntry lastEntry =(GeneralEntry) fileEntrySequence.getLastEntry();

        assertEquals(entry2, lastEntry);
    }
}
