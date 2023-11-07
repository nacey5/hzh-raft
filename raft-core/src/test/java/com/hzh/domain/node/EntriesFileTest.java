package com.hzh.domain.node;

import com.hzh.domain.log.entry.Entry;
import com.hzh.domain.log.entry.sepecific.GeneralEntry;
import com.hzh.domain.log.entry.sepecific.NoOpEntry;
import com.hzh.domain.log.sequence.EntriesFile;
import com.hzh.domain.log.sequence.EntryFactory;
import com.hzh.domain.node.file.ByteArraySeekableFile;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import static org.junit.Assert.*;

/**
 * @ClassName EntriesFileTest
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/7 11:21
 * @Version 0.0.1
 **/
public class EntriesFileTest {

    ByteArraySeekableFile seekableFile;

    @Before
    public void setUp() {
        seekableFile = new ByteArraySeekableFile();
    }

    @Test
    public void testAppendEntry() throws IOException {
        EntriesFile file = new EntriesFile(seekableFile);
        assertEquals(0L, file.appendEntry(new NoOpEntry(2, 3)));
        seekableFile.seek(0);
        assertEquals(Entry.KIND_OP_OP, seekableFile.readInt());
        assertEquals(2, seekableFile.readInt()); //index
        assertEquals(3, seekableFile.readInt()); //term
        assertEquals(0, seekableFile.readInt()); //command bytes length
        byte[] commandBytes = "test".getBytes();
        assertEquals(16L, file.appendEntry(new GeneralEntry(3, 3, commandBytes)));
        seekableFile.seek(16L);
        assertEquals(Entry.KIND_GENERAL, seekableFile.readInt());
        assertEquals(3, seekableFile.readInt()); //index
        assertEquals(3, seekableFile.readInt()); //term
        assertEquals(4, seekableFile.readInt()); //command bytes length
        byte[] buffer = new byte[4];
        seekableFile.read(buffer);
        assertArrayEquals(commandBytes, buffer);
    }

    @Test
    public void testLoadEntry() throws IOException {
        EntriesFile file = new EntriesFile(seekableFile);
        assertEquals(0l, file.appendEntry(new NoOpEntry(2, 3)));
        assertEquals(16L, file.appendEntry(new GeneralEntry(3, 3, "test".getBytes())));
        assertEquals(36L, file.appendEntry(new GeneralEntry(4, 3, "foo".getBytes())));
        EntryFactory factory = new EntryFactory();
        Entry entry = file.loadEntry(0L, factory);
        assertEquals(Entry.KIND_OP_OP, entry.getKind());
        assertEquals(2, entry.getIndex());
        assertEquals(3, entry.getTerm());
        entry = file.loadEntry(36L, factory);
        assertEquals(Entry.KIND_GENERAL, entry.getKind());
        assertEquals(4, entry.getIndex());
        assertEquals(3, entry.getTerm());
        assertArrayEquals("foo".getBytes(), entry.getCommandBytes());
    }

    @Test
    public void testTruncate() throws IOException {
        EntriesFile file = new EntriesFile(seekableFile);
        file.appendEntry(new NoOpEntry(2, 3));
        assertTrue(seekableFile.size()>0);
        file.truncate(0L);
        assertEquals(0L,seekableFile.size());
    }
}
