package com.hzh.domain.role.specific;

import com.hzh.domain.node.NodeId;
import com.hzh.domain.timer.ElectionTimeout;
import com.hzh.domain.role.AbstractNodeRole;
import com.hzh.domain.role.enums.RoleName;

/**
 * @ClassName Candidate
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/1 15:37
 * @Version 0.0.1
 **/
public class CandidateNodeRole extends AbstractNodeRole {

    private final int votesCount;

    private final ElectionTimeout electionTimeout;

    public CandidateNodeRole(int term, ElectionTimeout electionTimeout) {
        this(term,1,electionTimeout);
    }

    public CandidateNodeRole(int term, int votesCount, ElectionTimeout electionTimeout) {
        super(RoleName.CANDIDATE,term);
        this.votesCount = votesCount;
        this.electionTimeout = electionTimeout;
    }

    public int getVotesCount() {
        return votesCount;
    }


    @Override
    public void cancelTimeoutOrTask() {
        electionTimeout.cancel();
    }

    @Override
    public NodeId getLeaderId(NodeId selfId) {
        return null;
    }

    public CandidateNodeRole increaseVotesCount(ElectionTimeout electionTimeout){
        this.electionTimeout.cancel();
        return new CandidateNodeRole(term,votesCount+1,electionTimeout);
    }

    @Override
    public String toString() {
        return "Candidate{" +
                "votesCount=" + votesCount +
                ", electionTimeout=" + electionTimeout +
                ", term=" + term +
                '}';
    }
}

