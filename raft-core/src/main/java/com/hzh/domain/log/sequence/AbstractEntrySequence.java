package com.hzh.domain.log.sequence;

import com.hzh.domain.log.entry.Entry;
import com.hzh.domain.log.entry.EntryMeta;
import com.hzh.exception.EmptySequenceException;
import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * @ClassName AbstractEntrySequence
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/4 17:04
 * @Version 0.0.1
 **/

@Data
public abstract class AbstractEntrySequence implements EntrySequence {
    //Log index offset.
    protected int logIndexOffset;
    protected int nextLogIndex;

    public AbstractEntrySequence(int logIndexOffset) {
        this.logIndexOffset = logIndexOffset;
        this.nextLogIndex = logIndexOffset;
    }

    public boolean isEmpty() {
        return logIndexOffset == nextLogIndex;
    }

    // get first Log index,throw exception if is null
    public int getFirstLogIndex() {
        if (isEmpty()) {
            throw new EmptySequenceException("Log index is empty");
        }
        return doGetFirstLogIndex();
    }

    protected int doGetFirstLogIndex() {
        return logIndexOffset;
    }

    //get last Log index,throw exception if is null
    public int getLastLogIndex() {
        if (isEmpty()) {
            throw new EmptySequenceException("Log index is empty");
        }
        return doGetLastLogIndex();
    }

    protected int doGetLastLogIndex() {
        return nextLogIndex - 1;
    }

    public int getNextLogIndex() {
        return nextLogIndex;
    }

    //determine the log sequence exists of not
    public boolean isEntryPresent(int index) {
        return !isEmpty() && index >= doGetFirstLogIndex() && index <= doGetLastLogIndex();
    }

    public Entry getEntry(int index) {
        if (!isEntryPresent(index)) {
            return null;
        }
        return doGetEntry(index);
    }

    public EntryMeta getEntryMeta(int index) {
        Entry entry = getEntry(index);
        return entry != null ? entry.getMeta() : null;
    }

    public Entry getLastEntry() {
        return isEmpty() ? null : doGetEntry(doGetLastLogIndex());
    }

    public abstract Entry doGetEntry(int index);

    @Override
    public List<Entry> subList(int fromIndex) {
        if (isEmpty() || fromIndex > doGetLastLogIndex()) {
            return Collections.emptyList();
        }
        return subList(Math.max(fromIndex, doGetFirstLogIndex()), nextLogIndex);
    }

    @Override
    public List<Entry> subView(int fromIndex) {
        if (isEmpty() || fromIndex > doGetLastLogIndex()) {
            return Collections.emptyList();
        }
        return subList(Math.max(fromIndex, doGetFirstLogIndex()), nextLogIndex);
    }

    @Override
    public List<Entry> subList(int fromIndex, int toIndex) {
        if (isEmpty()) {
            throw new EmptySequenceException();
        }
        //check Index
        // I think if the subsequent boundary exceeds the List, it can be considered that all child nodes are returned.
        if (fromIndex < doGetFirstLogIndex() || fromIndex > toIndex) {
            throw new IllegalArgumentException("illegal from index" + fromIndex + " to index" + toIndex);
        }
        if (toIndex > doGetLastLogIndex() + 1) {
            return doSubList(fromIndex, doGetLastLogIndex() + 1);
        }
        return doSubList(fromIndex, toIndex);

    }

    // get a sub view
    protected abstract List<Entry> doSubList(int fromIndex, int toIndex);

    @Override
    public void append(List<Entry> entries) {
        for (Entry entry : entries) {
            append(entry);
        }
    }

    @Override
    public void append(Entry entry) {
        if (entry.getIndex() != nextLogIndex) {
            throw new IllegalArgumentException("entry index must be" + nextLogIndex);
        }
        doAppend(entry);
        //increase the log index
        nextLogIndex++;
    }

    protected abstract void doAppend(Entry entry);

    //remove the after of the index
    public void removeAfter(int index) {
        if (isEmpty() || index >= doGetLastLogIndex()) {
            return;
        }
        doRemoveAfter(index);
    }

    protected abstract void doRemoveAfter(int index);
}
