package com.hzh.domain.node.memory;

import com.hzh.domain.node.NodeId;
import com.hzh.domain.node.NodeStore;
import lombok.Data;

/**
 * @ClassName MemoryNodeStore
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/1 17:09
 * @Version 0.0.1
 **/

public class MemoryNodeStore implements NodeStore {

    private int term;

    private NodeId votedFor;

    public MemoryNodeStore(){
        this(0,null);
    }

    public MemoryNodeStore(int term, NodeId votedFor) {
        this.term=term;
        this.votedFor=votedFor;
    }

    @Override
    public int getTerm() {
        return term;
    }

    @Override
    public void setTerm(int term) {
        this.term=term;
    }

    @Override
    public void setVoteFor(NodeId voteFor) {
        this.votedFor=voteFor;
    }

    @Override
    public NodeId getVoteFor() {
        return votedFor;
    }

    @Override
    public void close() {

    }
}
