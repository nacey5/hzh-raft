package com.hzh.domain.node;

/**
 * @ClassName NodeStore
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/1 17:07
 * @Version 0.0.1
 **/
public interface NodeStore {

    int getTerm();

    void setTerm(int term);

    void setVoteFor(NodeId voteFor);

    NodeId getVoteFor();

    void close();
}
