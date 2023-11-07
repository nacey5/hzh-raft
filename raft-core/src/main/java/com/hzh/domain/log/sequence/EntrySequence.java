package com.hzh.domain.log.sequence;

import com.hzh.domain.log.entry.Entry;
import com.hzh.domain.log.entry.EntryMeta;

import java.util.List;

/**
 * @ClassName EntrySequence
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/4 16:45
 * @Version 0.0.1
 * <p>
 * directory entry sequence
 **/
public interface EntrySequence {
    boolean isEmpty();

    int getFirstLogIndex();

    int getLastLogIndex();

    int getNextLogIndex();

    // get the sub Sequence View, to the last index
    List<Entry> subList(int fromIndex);

    List<Entry> subList(int fromIndex, int toIndex);

    boolean isEntryPresent(int index);

    EntryMeta getEntryMeta(int index);

    Entry getEntry(int index);

    Entry getLastEntry();

    List<Entry> subView(int fromIndex);

    void append(Entry entry);

    void append(List<Entry> entries);

    void commit(int index);

    int getCommitIndex();
    //remove the log after the index
    void removeAfter(int index);
    void close();
}
