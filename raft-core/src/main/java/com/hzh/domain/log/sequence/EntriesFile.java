package com.hzh.domain.log.sequence;

import com.hzh.domain.log.entry.Entry;
import com.hzh.domain.node.file.RandomAccessFileAdapter;
import com.hzh.domain.node.file.SeekableFile;
import com.hzh.exception.EmptySequenceException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @ClassName EntriesFile
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/5 12:38
 * @Version 0.0.1
 **/
public class EntriesFile {
    private final SeekableFile seekableFile;

    public EntriesFile(File file) throws FileNotFoundException {
        this(new RandomAccessFileAdapter(file));
    }

    public EntriesFile(SeekableFile seekableFile) {
        this.seekableFile = seekableFile;
    }

    //append the log sequence
    public long appendEntry(Entry entry) throws IOException {
        long offset = seekableFile.size();
        //must check the entry
        if (entry==null){
            throw new EmptySequenceException("the entry must not be null");
        }
        seekableFile.seek(offset);
        seekableFile.writeInt(entry.getKind());
        seekableFile.writeInt(entry.getIndex());
        seekableFile.writeInt(entry.getTerm());
        byte[] commandBytes = entry.getCommandBytes();
        seekableFile.writeInt(commandBytes.length);
        seekableFile.write(commandBytes);
        return offset;
    }

    //load log entries from specified offset
    public Entry loadEntry(long offset, EntryFactory factory) throws IOException {
        if (offset > seekableFile.size()) {
            throw new IllegalArgumentException("offset > size");
        }
        seekableFile.seek(offset);
        int kind=seekableFile.readInt();
        int index=seekableFile.readInt();
        int term=seekableFile.readInt();
        int length=seekableFile.readInt();
        byte[] commandBytes=new byte[length];
        seekableFile.read(commandBytes);
        return factory.create(kind,index,term,commandBytes);
    }

    public long size() throws IOException {
        return seekableFile.size();
    }

    public void clear() throws IOException{
        truncate(0L);
    }

    private void truncate(long offset) throws IOException {
        seekableFile.truncate(offset);
    }

    public void close() throws IOException {
        seekableFile.close();
    }
}
