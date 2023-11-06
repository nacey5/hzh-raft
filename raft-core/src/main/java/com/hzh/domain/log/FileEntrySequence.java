package com.hzh.domain.log;

import com.hzh.domain.log.entry.Entry;
import com.hzh.domain.log.entry.EntryMeta;
import com.hzh.domain.log.sequence.AbstractEntrySequence;
import com.hzh.domain.log.sequence.EntriesFile;
import com.hzh.domain.log.sequence.EntryFactory;
import com.hzh.domain.log.sequence.EntryIndexFile;
import com.hzh.exception.LogException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * must test with logDir
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
        if (!pendingEntries.isEmpty()) {
            int firstPendingEntryIndex = pendingEntries.getFirst().getIndex();
            if (index >= firstPendingEntryIndex) {
                return pendingEntries.get(index - firstPendingEntryIndex);
            }
        }
        assert !entryIndexFile.isEmpty();
        return getEntryInFile(index);
    }

    @Override
    protected List<Entry> doSubList(int fromIndex, int toIndex) {
        List<Entry> result = new ArrayList<>();
        //get the log from the directory
        if (!entryIndexFile.isEmpty() && fromIndex <= entryIndexFile.getMaxEntryIndex()) {
            int maxIndex = Math.min(entryIndexFile.getMaxEntryIndex() + 1, toIndex);
            for (int i = fromIndex; i < maxIndex; i++) {
                result.add(getEntryInFile(i));
            }
        }
        //get the log from buffer
        if (!pendingEntries.isEmpty() && toIndex > pendingEntries.getFirst().getIndex()) {
            Iterator<Entry> iterator = pendingEntries.iterator();
            Entry entry;
            int index;
            while (iterator.hasNext()) {
                entry = iterator.next();
                index = entry.getIndex();
                if (index >= toIndex) {
                    break;
                }
                if (index >= fromIndex) {
                    result.add(entry);
                }
            }
        }
        return result;
    }

    private Entry getEntryInFile(int index) {
        long offset = entryIndexFile.getOffset(index);
        try {
            return entriesFile.loadEntry(offset, entryFactory);


        } catch (IOException e) {
            throw new LogException("failed to load entry" + index, e);
        }
    }

    @Override
    protected void doAppend(Entry entry) {
        pendingEntries.add(entry);
    }

    @Override
    protected void doRemoveAfter(int index) {
        //remove the log from buffer
        if (!pendingEntries.isEmpty() && index >= pendingEntries.getFirst().getIndex() - 1) {
            //Remove a specified number of log entries
            //The direction of circulation is from small to large, but removal is from back to front.
            //Finally removes the specified number of log entries
            for (int i = index + 1; i <= doGetLastLogIndex(); i++) {
                pendingEntries.removeLast();
            }
            nextLogIndex = index + 1;
            return;
        }
        try {
            if (index >= doGetFirstLogIndex()) {
                //The index is smaller than the first log in the log Suichong
                pendingEntries.clear();
                entriesFile.truncate(entryIndexFile.getOffset(index + 1));
                entryIndexFile.removeAfter(index);
                nextLogIndex = index + 1;
                commitIndex = index;
            } else {
                //If the index is smaller than the index of the first log, delete all data
                pendingEntries.clear();
                entriesFile.clear();
                entryIndexFile.clear();
                nextLogIndex = logIndexOffset;
                commitIndex = logIndexOffset - 1;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void commit(int index) {
        // check commit
        if (index <= commitIndex) {
            throw new IllegalArgumentException("commit index < " + commitIndex);
        }
        if (index == commitIndex) {
            return;
        }
        //If commitIndex is in file ,only updates commitIndex
        if (!entryIndexFile.isEmpty() && index <= entryIndexFile.getMaxEntryIndex()) {
            commitIndex = index;
            return;
        }
        //Check whether commitIndex is within the range of log buffer
        if (pendingEntries.isEmpty() ||
                pendingEntries.getFirst().getIndex() > index ||
                pendingEntries.getLast().getIndex() < index) {
            throw new IllegalArgumentException("no entry to commit or commit index exceed");
        }
        long offset;
        Entry entry = null;
        try {
            for (int i = pendingEntries.getFirst().getIndex(); i < index; i++) {
                entry = pendingEntries.removeFirst();
                offset = entriesFile.appendEntry(entry);
                entryIndexFile.appendEntryIndex(i, offset, entry.getKind(), entry.getTerm());
                commitIndex = i;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getCommitIndex() {
        return commitIndex;
    }

    @Override
    public void close() {

    }

    @Override
    public EntryMeta getEntryMeta(int index) {
        if ((!isEntryPresent(index))) {
            return null;
        }
        if (!pendingEntries.isEmpty()) {
            int firstPendingEntryIndex = pendingEntries.getFirst().getIndex();
            if (index >= firstPendingEntryIndex) {
                return pendingEntries.get(index - firstPendingEntryIndex).getMeta();
            }
        }

        return entryIndexFile.get(index).toEntryMeta();
    }

    // get the last log
    public Entry getLastEntry() {
        if (isEmpty()) {
            return null;
        }
        if (!pendingEntries.isEmpty()) {
            return pendingEntries.getLast();
        }
        assert !entryIndexFile.isEmpty();
        return getEntryInFile(entryIndexFile.getMaxEntryIndex());
    }
}
