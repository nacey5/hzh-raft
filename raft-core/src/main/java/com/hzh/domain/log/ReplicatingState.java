package com.hzh.domain.log;

import lombok.Data;

/**
 * @ClassName ReplicatingState
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/2 11:19
 * @Version 0.0.1
 **/
@Data
public class ReplicatingState {

    private int nextIndex;
    private int matchIndex;
    private boolean replicating = false;
    private long lastReplicatedAt = 0;

    ReplicatingState(int nextIndex) {
        this(nextIndex, 0);
    }

    ReplicatingState(int nextIndex, int matchIndex) {
        this.nextIndex = nextIndex;
        this.matchIndex = matchIndex;
    }

    boolean backOffNextIndex() {
        if (nextIndex > 1) {
            nextIndex--;
            return true;
        }
        return false;
    }

    /**
     * Advance next index and match index by last entry index.
     * @param lastEntryIndex
     * @return true if advanced, false if no change
     */
    boolean advance(int lastEntryIndex) {
        // changed
        boolean result = (matchIndex != lastEntryIndex || nextIndex != (lastEntryIndex + 1));

        matchIndex = lastEntryIndex;
        nextIndex = lastEntryIndex + 1;

        return result;
    }
}
