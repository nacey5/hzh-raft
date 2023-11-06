package com.hzh.domain.node;

import com.hzh.domain.log.ReplicatingState;
import lombok.Data;

/**
 * @ClassName GroupMember
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/1 20:27
 * @Version 0.0.1
 **/
@Data
public class GroupMember {
    private final NodeEndpoint endpoint;
    private ReplicatingState replicatingState;

    public GroupMember(NodeEndpoint endpoint, ReplicatingState replicatingState) {
        this.endpoint = endpoint;
        this.replicatingState = replicatingState;
    }

    public GroupMember(NodeEndpoint endpoint) {
        this(endpoint, null);
    }

    NodeId getId() {
        return endpoint.getId();
    }

    boolean idEquals(NodeId id) {
        return endpoint.getId().equals(id);
    }

    public int getNextIndex() {
        return ensureReplicatingState().getNextIndex();
    }

    private ReplicatingState ensureReplicatingState() {
        if (replicatingState == null) {
            throw new IllegalStateException("replication state not set");
        }
        return replicatingState;
    }

    public boolean advanceReplicatingState(int lastEntryIndex) {
        return false;
    }

    int getMatchIndex() {
        return ensureReplicatingState().getMatchIndex();
    }

    boolean backOffNextIndex() {
        return ensureReplicatingState().backOffNextIndex();
    }
}
