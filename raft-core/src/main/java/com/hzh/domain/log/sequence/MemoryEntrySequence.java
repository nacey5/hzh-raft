package com.hzh.domain.log.sequence;

import com.hzh.domain.log.entry.Entry;
import com.hzh.domain.log.sequence.AbstractEntrySequence;

import java.util.ArrayList;
import java.util.List;

/**
 * I must remind you here that in this system, your index does not start from 0 but from 1
 * @ClassName MemoryEntrySequence
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/5 12:10
 * @Version 0.0.1
 **/
public class MemoryEntrySequence extends AbstractEntrySequence {
    private final List<Entry> entries = new ArrayList<>();
    private int commitIndex = 0;

    public MemoryEntrySequence() {
        this(1);
    }

    public MemoryEntrySequence(int logIndexOffset) {
        super(logIndexOffset);
    }

    @Override
    public Entry doGetEntry(int index) {
        return entries.get(index - logIndexOffset);
    }

    @Override
    protected List<Entry> doSubList(int fromIndex, int toIndex) {
        return entries.subList(fromIndex - logIndexOffset, toIndex - logIndexOffset);
    }

    @Override
    protected void doAppend(Entry entry) {
        entries.add(entry);
    }


    @Override
    protected void doRemoveAfter(int index) {
        if (index < doGetFirstLogIndex()) {
            entries.clear();
            nextLogIndex = logIndexOffset;
        } else {
            entries.subList(index - logIndexOffset + 1, entries.size()).clear();
            nextLogIndex = index + 1;
        }
    }

    @Override
    public void commit(int index) {
        commitIndex = index;
    }

    @Override
    public int getCommitIndex() {
        return commitIndex;
    }

    @Override
    public void close() {

    }

    @Override
    public String toString() {
        return "MemoryEntrySequence{" +
                "logIndexOffset=" + logIndexOffset +
                ", nextLogIndex=" + nextLogIndex +
                ",entries.size=" + entries.size() +
                '}';
    }
}
