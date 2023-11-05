package com.hzh.domain.log;

import com.hzh.domain.log.entry.Entry;
import com.hzh.domain.log.sequence.AbstractEntrySequence;
import com.hzh.domain.log.sequence.EntriesFile;
import com.hzh.domain.log.sequence.EntryFactory;
import com.hzh.domain.log.sequence.EntryIndexFile;
import com.hzh.exception.LogException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @ClassName FileEntrySequence
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/5 16:57
 * @Version 0.0.1
 **/
public class FileEntrySequence extends AbstractEntrySequence {

    private final EntryFactory entryFactory = new EntryFactory();
    private final EntriesFile entriesFile;
    private final EntryIndexFile entryIndexFile;
    private final LinkedList<Entry> pendingEntries = new LinkedList<>();

    //The initial commitIndex defined in the Raft algorithm has nothing to do with whether the log is persisted or not.
    private int commitIndex = 0;

    //construct function,aim to the directory
    public FileEntrySequence(LogDir logDir, int logIndexOffset) {
        super(logIndexOffset);
        try {
            this.entriesFile = new EntriesFile(logDir.getEntryOffsetIndexFile());
            entryIndexFile = new EntryIndexFile(logDir.getEntryOffsetIndexFile());
            initialize();
        } catch (IOException e) {
            throw new LogException("failed to oen entries file or entry index file", e);
        }
    }

    public FileEntrySequence(EntriesFile entriesFile, EntryIndexFile entryIndexFile, int logIndexOffset) {
        super(logIndexOffset);
        this.entriesFile = entriesFile;
        this.entryIndexFile = entryIndexFile;
        initialize();
    }

    private void initialize() {
        if (entryIndexFile.isEmpty()) {
            return;
        }
        //Use minEntryIndex of the log index file as logIndexOffset
        logIndexOffset = entryIndexFile.getMinEntryIndex();
        nextLogIndex = entryIndexFile.getMaxEntryIndex() + 1;
    }


    @Override
    public Entry doGetEntry(int index) {
        return null;
    }

    @Override
    protected List<Entry> doSubList(int fromIndex, int toIndex) {
        return null;
    }

    @Override
    protected void doAppend(Entry entry) {

    }

    @Override
    protected void doRemoveAfter(int index) {

    }

    @Override
    public void commit(int index) {

    }

    @Override
    public int getCommitIndex() {
        return commitIndex;
    }

    @Override
    public void close() {

    }
}
