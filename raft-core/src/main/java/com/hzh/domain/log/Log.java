package com.hzh.domain.log;

import com.hzh.domain.log.entry.Entry;
import com.hzh.domain.log.entry.EntryMeta;
import com.hzh.domain.log.entry.sepecific.GeneralEntry;
import com.hzh.domain.log.entry.sepecific.NoOpEntry;
import com.hzh.domain.message.AppendEntriesRpc;
import com.hzh.domain.node.NodeId;

import java.util.List;

/**
 * @ClassName Log
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/4 16:31
 * @Version 0.0.1
 * this is interface for all the log operation,
 * includes the add log ,append,advance and check
 **/
public interface Log {
    int ALL_ENTRIES=1;
    // get the metadata for the lastIndex
    EntryMeta getLastEntryMeta();
    AppendEntriesRpc createAppendEntriesRpc(int term, NodeId selfId,int nextIndex,int maxEntries);
    int getNextIndex();
    boolean isNewerThan(int latLogIndex,int lastLogTerm);
    NoOpEntry appendEntry(int term);
    GeneralEntry appendEntry(int term,byte[] command);
    //append log from leader
    boolean appendEntriesFromLeader(int prevLogIndex, int prevLogTerm, List<Entry> entries);

    void advanceCommitIndex(int newCommitIndex,int currentTerm);
    //void setStateMachine(StateMachine stateMachine);
    void close();
}
