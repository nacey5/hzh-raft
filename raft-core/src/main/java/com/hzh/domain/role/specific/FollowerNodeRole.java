package com.hzh.domain.role.specific;

import com.hzh.domain.timer.ElectionTimeout;
import com.hzh.domain.node.NodeId;
import com.hzh.domain.role.AbstractNodeRole;
import com.hzh.domain.role.enums.RoleName;

/**
 * @ClassName Follower
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/1 15:24
 * @Version 0.0.1
 **/

public class FollowerNodeRole extends AbstractNodeRole {
    private final NodeId votedFor;

    private final NodeId leaderId;

    //选举超时
    private final ElectionTimeout electionTimeout;

    public FollowerNodeRole(int term, NodeId votedFor, NodeId leaderId, ElectionTimeout electionTimeout) {
        super(RoleName.FOLLOWER,term);
        this.votedFor = votedFor;
        this.leaderId = leaderId;
        this.electionTimeout=electionTimeout;

    }

    @Override
    public void cancelTimeoutOrTask(){
        electionTimeout.cancel();
    }


    public NodeId getVotedFor(){
        return votedFor;
    }

    public NodeId getLeaderId() {
        return leaderId;
    }

    @Override
    public String toString() {
        return "Follower{" +
                "votedFor=" + votedFor +
                ", leaderId=" + leaderId +
                ", electionTimeout=" + electionTimeout +
                ", term=" + term +
                '}';
    }
}
