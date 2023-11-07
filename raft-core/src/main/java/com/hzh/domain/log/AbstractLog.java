package com.hzh.domain.log;

import com.hzh.domain.log.entry.Entry;
import com.hzh.domain.log.entry.EntryMeta;
import com.hzh.domain.log.entry.sepecific.GeneralEntry;
import com.hzh.domain.log.entry.sepecific.NoOpEntry;
import com.hzh.domain.log.sequence.EntrySequence;
import com.hzh.domain.message.AppendEntriesRpc;
import com.hzh.domain.node.NodeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @ClassName AbstractLog
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/6 11:02
 * @Version 0.0.1
 **/
public class AbstractLog implements Log {

    private final static Logger logger = LoggerFactory.getLogger(AbstractLog.class);
    protected static EntrySequence entrySequence;

    protected int commitIndex = 0;

    @Override
    public EntryMeta getLastEntryMeta() {
        if (entrySequence.isEmpty()) {
            return new EntryMeta(Entry.KIND_OP_OP, 0, 0);
        }
        return entrySequence.getLastEntry().getMeta();
    }

    //Create appendEntries message
    @Override
    public AppendEntriesRpc createAppendEntriesRpc(int term, NodeId selfId, int nextIndex, int maxEntries) {
        //check the nextIndex
        int nextLogIndex = entrySequence.getNextLogIndex();
        if (nextIndex > nextLogIndex) {
            throw new IllegalArgumentException("illegal next index " + nextIndex);
        }
        AppendEntriesRpc rpc = new AppendEntriesRpc();
        rpc.setTerm(term);
        rpc.setLeaderId(selfId);
        rpc.setLeaderCommit(commitIndex);
        //Set the meta information of the previous log, which may not exist
        Entry entry = entrySequence.getEntry(nextIndex - 1);
        if (entry != null) {
            rpc.setPreLogIndex(entry.getIndex());
            rpc.setPreLogTerm(entry.getTerm());
        }
        //set the entries
        if (!entrySequence.isEmpty()) {
            int maxIndex = (maxEntries == ALL_ENTRIES ? nextLogIndex : Math.min(nextLogIndex, nextIndex + maxEntries));
            rpc.setEntries(entrySequence.subList(nextIndex, maxIndex));
        }
        return rpc;
    }

    @Override
    public int getNextIndex() {
        return 0;
    }

    @Override
    public boolean isNewerThan(int lastLogIndex, int lastLogTerm) {
        EntryMeta lastEntryMeta = getLastEntryMeta();
        logger.debug("last entry ({}, {}), candidate ({}, {})", lastEntryMeta.getIndex(), lastEntryMeta.getTerm());
        return lastEntryMeta.getTerm() > lastLogTerm || lastEntryMeta.getIndex() > lastLogIndex;
    }

    @Override
    public NoOpEntry appendEntry(int term) {
        NoOpEntry entry = new NoOpEntry(entrySequence.getNextLogIndex(), term);
        entrySequence.append(entry);
        return entry;
    }

    @Override
    public GeneralEntry appendEntry(int term, byte[] command) {
        GeneralEntry entry = new GeneralEntry(entrySequence.getNextLogIndex(), term, command);
        entrySequence.append(entry);
        return entry;
    }

    @Override
    public boolean appendEntriesFromLeader(int prevLogIndex, int prevLogTerm, List<Entry> leaderEntries) {
        // Check whether the previous log matches
        if (!checkIfPreviousLogMatches(prevLogIndex, prevLogTerm)) {
            return false;
        }
        // the node from leader is empty
        if (leaderEntries.isEmpty()) {
            return true;
        }
        //Remove conflicting log entries and return the next log entry to append (if any)
        EntrySequenceView newEntries = removeUnmatchedLog(new EntrySequenceView(leaderEntries));
        // append log only
        appendEntriesFromLeader(newEntries);
        return true;
    }

    private EntrySequenceView removeUnmatchedLog(EntrySequenceView leaderEntries) {
        assert !leaderEntries.isEmpty();
        int firstUnmatched = findFirstUnmatchedLog(leaderEntries);
        removeEntriesAfter(firstUnmatched - 1);
        return leaderEntries.subView(firstUnmatched);
    }


    private int findFirstUnmatchedLog(EntrySequenceView leaderEntries) {
        assert !leaderEntries.isEmpty();
        int logIndex;
        EntryMeta followerEntryMeta;
        for (Entry leaderEntry : leaderEntries) {
            logIndex = leaderEntry.getIndex();
            followerEntryMeta = entrySequence.getEntryMeta(logIndex);
            if (followerEntryMeta == null || followerEntryMeta.getTerm() != leaderEntry.getTerm()) {
                return logIndex;
            }
        }
        return leaderEntries.getLastLogIndex() + 1;
    }

    // todo It involves conflict status, that is, term of office, which will not be dealt with at this stage.
    private void removeEntriesAfter(int index){
        if (entrySequence.isEmpty() || index >= entrySequence.getLastLogIndex()) {
            return;
        }
        logger.debug("remove entries after {}", index);
        entrySequence.removeAfter(index);
        if (index < commitIndex) {
            commitIndex = index;
        }
    }

    private void appendEntriesFromLeader(EntrySequenceView leaderEntries) {
        if (leaderEntries.isEmpty()) {
            return;
        }
        logger.debug("append entries from leader from {} to {}", leaderEntries.getFirstLogIndex(), leaderEntries.getLastLogIndex());
        for (Entry leaderEntry : leaderEntries) {
            entrySequence.append(leaderEntry);
        }
    }


    private boolean checkIfPreviousLogMatches(int prevLogIndex, int prevLogTerm) {
        //Check log entries for specified index
        EntryMeta meta = entrySequence.getEntryMeta(prevLogIndex);
        // the log not exists
        if (meta == null) {
            logger.debug("previous log {} not exists", prevLogIndex);
            return false;
        }
        int term = meta.getTerm();
        if (term != prevLogTerm) {
            logger.debug("different term of previous log, local {} ,remote {}", term, prevLogTerm);
            return false;
        }
        return true;
    }

    @Override
    public void advanceCommitIndex(int newCommitIndex, int currentTerm) {
        if (!validateNewCommitIndex(newCommitIndex, currentTerm)) {
            return;
        }
        logger.debug("advance commit index from {} to {}", commitIndex, newCommitIndex);
        //todo advanceApplyIndex();
    }

    private boolean validateNewCommitIndex(int newCommitIndex, int currentTerm) {
        // <now's commitIndex
        if (newCommitIndex <= entrySequence.getCommitIndex()){
            return false;
        }
        EntryMeta meta=entrySequence.getEntryMeta(newCommitIndex);
        if (meta==null){
            logger.debug("log of new commit index {} not found",newCommitIndex);
            return false;
        }
        // log's term must be the now term, it can be advance if like this
        if (meta.getTerm()!= currentTerm){
            logger.debug("log of new commit index != current term({} != {})",meta.getTerm(),currentTerm);
            return false;
        }
        return true;
    }

    @Override
    public void close() {

    }


    private static class EntrySequenceView implements Iterable<Entry> {
        private final List<Entry> entries;
        private int firstLogIndex = -1;
        private int lastLogIndex = -1;

        //construct
        EntrySequenceView(List<Entry> entries) {
            this.entries = entries;
            if (!entries.isEmpty()) {
                firstLogIndex = entries.get(0).getIndex();
                lastLogIndex = entries.get(entries.size() - 1).getIndex();
            }
        }

        Entry get(int index) {
            if (entries.isEmpty() | index < firstLogIndex || index > lastLogIndex) {
                return null;
            }
            return entries.get(index - firstLogIndex);
        }

        boolean isEmpty() {
            return entries.isEmpty();
        }

        //get the fist index from log ,and doesn't check the null
        int getFirstLogIndex() {
            return firstLogIndex;
        }

        int getLastLogIndex() {
            return lastLogIndex;
        }

        // get the sub sequence view
        EntrySequenceView subView(int fromIndex) {
            if (entries.isEmpty() || fromIndex > lastLogIndex) {
                return new EntrySequenceView(Collections.emptyList());
            }
            return new EntrySequenceView(entries.subList(fromIndex - firstLogIndex, entries.size()));
        }

        @Override
        @Nonnull
        public Iterator<Entry> iterator() {
            return entries.iterator();
        }

        private EntrySequenceView removeUnmatchedLog(EntrySequenceView leaderEntries) {
            // the node from leader doesn't empty
            assert !leaderEntries.isEmpty();
            //find first not match log
            int firstUnmatched = findFirstUnmatchedLog(leaderEntries);
            // hava not match log
            if (firstUnmatched < 0) {
                return new EntrySequenceView(Collections.emptyList());
            }
            //remove all the log from doesn't match
            removeEntriesAfter(firstUnmatched - 1);
            //returns the log entries appended later
            return leaderEntries.subView(firstLogIndex);
        }

        private void removeEntriesAfter(int index) {
            if (entrySequence.isEmpty() || index >= entrySequence.getLastLogIndex()) {
                return;
            }
            //Note that if you remove the applied log here, you need to build the state machine from scratch
            logger.debug("remove entries after {}", index);
            entrySequence.removeAfter(index);
        }

        private int findFirstUnmatchedLog(EntrySequenceView leaderEntries) {
            int logIndex;
            EntryMeta followerEntryMeta;
            for (Entry leaderEntry : leaderEntries) {
                logIndex = leaderEntry.getIndex();
                //Find log entry meta information by index
                followerEntryMeta = entrySequence.getEntryMeta(logIndex);
                // the log not exists or the term not match
                if (followerEntryMeta == null || followerEntryMeta.getTerm() != leaderEntry.getTerm()) {
                    return logIndex;
                }
            }
            return -1;
        }
    }
}
