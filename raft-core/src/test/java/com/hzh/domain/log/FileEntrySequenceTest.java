package com.hzh.domain.log;

import com.hzh.domain.log.entry.Entry;
import com.hzh.domain.log.entry.sepecific.NoOpEntry;
import com.hzh.domain.log.sequence.EntriesFile;
import com.hzh.domain.log.sequence.EntryIndexFile;
import com.hzh.domain.node.file.ByteArraySeekableFile;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


@RunWith(MockitoJUnitRunner.class)
public class FileEntrySequenceTest {
    private EntriesFile entriesFile;
    private EntryIndexFile entryIndexFile;

    @Before
    public void setUp() throws Exception {
        entriesFile = new EntriesFile(new ByteArraySeekableFile());
        entryIndexFile = new EntryIndexFile(new ByteArraySeekableFile());
    }

    @Test
    public void testInitialize() throws IOException {
        entryIndexFile.appendEntryIndex(1, 0L, 1, 1);
        entryIndexFile.appendEntryIndex(2, 20L, 1, 1);
        FileEntrySequence sequence = new FileEntrySequence(entriesFile, entryIndexFile, 1);
        assertEquals(3, sequence.getNextLogIndex());
        assertEquals(1, sequence.getFirstLogIndex());
        assertEquals(2, sequence.getLastLogIndex());
        assertEquals(2, sequence.getCommitIndex());
    }

    @Test
    public void testAppendEntry() {
        FileEntrySequence sequence = new FileEntrySequence(entriesFile, entryIndexFile, 1);
        assertEquals(1, sequence.getNextLogIndex());
        sequence.append(new NoOpEntry(1, 1));
        assertEquals(2, sequence.getNextLogIndex());
        assertEquals(1, sequence.getLastEntry().getIndex());
    }

    private void appendEntryToFile(Entry entry) throws IOException{
        long offset=entriesFile.appendEntry(entry);
        entryIndexFile.appendEntryIndex(entry.getIndex(),offset,entry.getKind(),entry.getTerm());
    }

    @Test
    public void testGetEntry() throws IOException{
        appendEntryToFile(new NoOpEntry(1,1));
        FileEntrySequence sequence = new FileEntrySequence(entriesFile, entryIndexFile, 1);
        sequence.append(new NoOpEntry(2,1));
        assertNull(sequence.getEntry(0));
        assertEquals(1,sequence.getEntry(1).getIndex());
        assertEquals(2,sequence.getEntry(2).getIndex());
        assertNull(sequence.getEntry(3));
    }

    @Test
    public void testSubList2() throws IOException{
        appendEntryToFile(new NoOpEntry(1,1)); //1
        appendEntryToFile(new NoOpEntry(2,2));// 2
        FileEntrySequence sequence = new FileEntrySequence(entriesFile, entryIndexFile, 1);
        sequence.append(new NoOpEntry(sequence.getNextLogIndex(),3)); //3
        sequence.append(new NoOpEntry(sequence.getNextLogIndex(),4)); //4

        List<Entry> subList=sequence.subView(2);
        assertEquals(3,subList.size());
        assertEquals(2,subList.get(0).getIndex());
        assertEquals(4,subList.get(2).getIndex());
    }

    @Test
    public void testRemoveAfterEntriesInFile2() throws IOException{
        appendEntryToFile(new NoOpEntry(1,1)); //1
        appendEntryToFile(new NoOpEntry(2,1)); //2
        FileEntrySequence sequence = new FileEntrySequence(entriesFile, entryIndexFile, 1);
        sequence.append(new NoOpEntry(3,2)); //3
        assertEquals(1,sequence.getFirstLogIndex());
        assertEquals(3,sequence.getLastLogIndex());
        sequence.removeAfter(1);
        assertEquals(1,sequence.getFirstLogIndex());
        assertEquals(1,sequence.getLastLogIndex());
    }
}
