package com.hzh.domain.node;

import com.google.common.eventbus.Subscribe;
import com.hzh.context.NodeContext;
import com.hzh.domain.log.entry.EntryMeta;
import com.hzh.domain.log.task.LogReplicationTask;
import com.hzh.domain.message.*;
import com.hzh.domain.role.AbstractNodeRole;
import com.hzh.domain.role.enums.RoleName;
import com.hzh.domain.role.specific.CandidateNodeRole;
import com.hzh.domain.role.specific.FollowerNodeRole;
import com.hzh.domain.role.specific.LeaderNodeRole;
import com.hzh.domain.timer.ElectionTimeout;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;


/**
 * 这里我没使用volatile修饰，因为跑的是单个线程的线程池，如果是多线程，
 * 则需要使用volatile修饰，因为多线程下，volatile修饰的变量，在多线程下是可见的。
 *
 * @ClassName NodeImpl
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/1 19:33
 * @Version 0.0.1
 **/

@Data
public class NodeImpl implements Node {

    public static final Logger logger = LoggerFactory.getLogger(NodeImpl.class);

    private final NodeContext context;

    private boolean started;

    private AbstractNodeRole role;

    public NodeImpl(NodeContext context) {
        this.context = context;
    }

    @Override
    public synchronized void start() {
        if (started) {
            return;
        }
        //register to the eventBus
        context.getEventBus().register(this);
        logger.info("the node {} has subscriber the event bus",context.getSelfId());
        //init the connector
        context.getConnector().initialize();
        //when it start started,the role is Follower
        NodeStore store = context.getStore();
        changeToRole(new FollowerNodeRole(store.getTerm(), store.getVoteFor(), null, scheduleElectionTimeout()));
        started = true;
    }


    private void changeToRole(AbstractNodeRole newRole) {
        logger.debug("node{},role state changed->{}", context.getSelfId(), newRole);
        NodeStore store = context.getStore();
        store.setTerm(newRole.getTerm());
        if (newRole.getName() == RoleName.FOLLOWER) {
            store.setVoteFor(((FollowerNodeRole) newRole).getVotedFor());
        }
        role = newRole;
    }

    private ElectionTimeout scheduleElectionTimeout() {
        return context.getScheduler().scheduleElectionTimeout(this::electionTimeout);
    }

    //election Timeout
    public void electionTimeout() {
        context.getTaskExecutor().submit(this::doProcessElectionTimeout);
    }

    private void doProcessElectionTimeout() {
        EntryMeta lastEntryMeta =context.getLog().getLastEntryMeta();
        //There is no election start time under the Leader role.
        if (role.getName() == RoleName.LEADER) {
            logger.warn("node{},election timeout,but it is leader,ignore", context.getSelfId());
            return;
        }
        //For follower nodes, it initiates an election
        //For the candidate node, the election is initiated again
        //term+1
        int newTerm = role.getTerm() + 1;
        role.cancelTimeoutOrTask();
        logger.info("start election");
        //become to the Candidate
        changeToRole(new CandidateNodeRole(newTerm, scheduleElectionTimeout()));

        //send the RequestVote message
        RequestVoteRpc rpc = new RequestVoteRpc();
        rpc.setTerm(newTerm);
        rpc.setCandidateId(context.getSelfId());
        rpc.setLastLogIndex(lastEntryMeta.getIndex());
        rpc.setLastLogTerm(lastEntryMeta.getTerm());
        context.getConnector().sendRequestVote(rpc, context.getGroup().listEndpointExceptSelf());
    }

    @Override
    public synchronized void stop() throws InterruptedException {
        //not allow stop when it not start
        if (!started) {
            throw new IllegalStateException("node not started");
        }
        context.getScheduler().stop();
        context.getConnector().close();
        context.getTaskExecutor().shutdown();
        started = false;
    }

    @Subscribe
    public void onReceiveRequestVoteRpc(RequestVoteRpcMessage rpcMessage) {
        context.getTaskExecutor().submit(
                () -> context.getConnector().replyRequestVote(
                        doProcessRequestVoteRpc(rpcMessage),
                        context.findMember(rpcMessage.getSourceId()).getEndpoint()
                )
        );
    }

    private RequestVoteResult doProcessRequestVoteRpc(RequestVoteRpcMessage rpcMessage) {
        //If the other party's term is smaller than your own, do not vote and return your own term to the object
        RequestVoteRpc rpc = rpcMessage.get();
        if (rpc.getTerm() < role.getTerm()) {
            logger.debug("[{}] term is smaller than my term, do not vote ({}<{})", rpc.getTerm(), rpc.getTerm(), role.getTerm());
            return new RequestVoteResult(role.getTerm(), false);
        }
        //Vote unconditionally here
        boolean votedForCandidate = true;
        //If the object's term is larger than itself, switch to the Follower role
        if (rpc.getTerm() > role.getTerm()) {
            boolean voteForCandidate = !context.getLog().isNewerThan(rpc.getLastLogIndex(), rpc.getLastLogTerm());
            becomeFollower(rpc.getTerm(), (votedForCandidate ? rpc.getCandidateId() : null), null, true);
            return new RequestVoteResult(rpc.getTerm(), votedForCandidate);
        }
        //The local term is consistent with the message term
        switch (role.getName()) {
            case FOLLOWER:
                FollowerNodeRole follower = (FollowerNodeRole) role;
                NodeId votedFor = follower.getVotedFor();
                //Vote in the following two situations
                //Case 1: have not invested in millet yet, and the other party’s log is newer than yours.
                //You need to switch to the Follower role after voting
                //Case 2: have already submitted to the other party
                if ((votedFor == null && votedForCandidate) || //case1
                        Objects.equals(votedFor, rpc.getCandidateId())) { //case2
                    becomeFollower(role.getTerm(), rpc.getCandidateId(), null, true);
                    return new RequestVoteResult(rpc.getTerm(), true);
                }
                return new RequestVoteResult(role.getTerm(), false);
            case CANDIDATE://Have already voted for myself, so I will not vote for other nodes.
            case LEADER:
                return new RequestVoteResult(role.getTerm(), false);
            default:
                throw new IllegalStateException("unexpected node role [" + role.getName() + "]");
        }

    }

    private void becomeFollower(int term, NodeId votedFor, NodeId leaderId, boolean scheduleElectionTimeout) {
        //cancel the timer for the election timout
        role.cancelTimeoutOrTask();
        if (leaderId != null && !leaderId.equals(role.getLeaderId(context.getSelfId()))) {
            logger.info("current leader is{},term {}", leaderId, term);
        }
        //Recreate the election running timer or empty timer
        ElectionTimeout electionTimeout = scheduleElectionTimeout ? scheduleElectionTimeout() : ElectionTimeout.NONE;
        changeToRole(new FollowerNodeRole(term, votedFor, leaderId, electionTimeout));
    }

    @Subscribe
    public void onReceiveRequestVoteResult(RequestVoteResult result) {
        context.getTaskExecutor().submit(
                () -> doProcessRequestVoteResult(result)
        );
    }

    private void doProcessRequestVoteResult(RequestVoteResult result) {
        //If the object's term is larger than itself, it degenerates into the Fo!lower role
        if (result.getTerm() > role.getTerm()) {
            becomeFollower(result.getTerm(), null, null, true);
            return;
        }
        //Ignore if you are not a Can idaLe character
        if (role.getName() != RoleName.CANDIDATE) {
            logger.debug("receive request vote result and current role is not candidate,ignore");
        }
        //Ignore if the other party's ter is smaller than yourself or the object did not vote for yourself.
        if (result.getTerm() < role.getTerm() || !result.isVoteGranted()) {
            return;
        }
        // current voteCount
        int currentVotesCount = ((CandidateNodeRole) role).getVotesCount() + 1;
        // current nodeCount
        int countOfMajor = context.getGroup().getCount();
        logger.debug("votes count{},node count {}", currentVotesCount, countOfMajor);
        //Cancel the election timeout timer
        role.cancelTimeoutOrTask();
        if (currentVotesCount > countOfMajor / 2) {
            logger.info("become leader,term{}", role.getTerm());
            resetReplicatingStates();
            changeToRole(new LeaderNodeRole(role.getTerm(), scheduleLogReplicationTask()));
        } else {
            //Modify the number of votes received and recreate the election timeout timing
            changeToRole(new CandidateNodeRole(role.getTerm(), currentVotesCount, scheduleElectionTimeout()));
        }
    }

    private LogReplicationTask scheduleLogReplicationTask() {
        return context.getScheduler().scheduleLogReplicationTask(this::replicateLog);
    }

    private void resetReplicatingStates() {
        context.getGroup().resetReplicatingStates(context.getLog().getNextIndex());
    }

    public void replicateLog() {
        context.getTaskExecutor().submit(this::doReplicateLog);
    }

    private void doReplicateLog() {
        logger.debug("replicate log");
        //Send AppendEntries message to log replication target node
        for (GroupMember member : context.getGroup().listReplicationTargets()) {
            doReplicateLogForDetail(member,context.getConfig().getMaxReplicationEntries());
        }
    }

    private void doReplicateLogForDetail(GroupMember member) {
        AppendEntriesRpc rpc = new AppendEntriesRpc();
        rpc.setTerm(role.getTerm());
        rpc.setLeaderId(context.getSelfId());
        rpc.setPreLogIndex(0);
        rpc.setPreLogTerm(0);
        rpc.setLeaderCommit(0);
        context.getConnector().sendAppendEntries(rpc, member.getEndpoint());
    }

    private void doReplicateLogForDetail(GroupMember member, int maxEntries) {
        AppendEntriesRpc rpc = context.getLog().createAppendEntriesRpc(role.getTerm(), context.getSelfId(), member.getNextIndex(), maxEntries);
        context.getConnector().sendAppendEntries(rpc, member.getEndpoint());
    }


    @Subscribe
    public void onReceiveAppendEntriesRpc(AppendEntriesRpcMessage rpcMessage) {
        context.getTaskExecutor().submit(() ->
                context.getConnector().replyAppendEntries(
                        doProcessAppendEntriesRpc(rpcMessage),
                        //the node for the send message
                        context.findMember(rpcMessage.getSourceId()).getEndpoint()
                ));
    }

    private AppendEntriesResult doProcessAppendEntriesRpc(AppendEntriesRpcMessage rpcMessage) {
        AppendEntriesRpc rpc = rpcMessage.get();
        //If the other party's tenn is smaller than your own, reply to your own term
        if (rpc.getTerm() < role.getTerm()) {
            return new AppendEntriesResult(role.getTerm(), false);
        }
        //If the object's term is larger than itself, it will degenerate into the Follower role
        if (rpc.getTerm() > role.getTerm()) {
            becomeFollower(rpc.getTerm(), null, rpc.getLeaderId(), true);
            //append log
            return new AppendEntriesResult(rpc.getTerm(), appendEntries(rpc));
        }
        assert rpc.getTerm() == role.getTerm();
        switch (role.getName()) {
            case FOLLOWER:
                //set leaderId and reset port up timer
                becomeFollower(rpc.getTerm(), ((FollowerNodeRole) role).getVotedFor(), rpc.getLeaderId(), true);
                //append Log
                return new AppendEntriesResult(rpc.getTerm(), appendEntries(rpc));
            case CANDIDATE:
                ///If there are two Candidate roles and the other Candidate becomes the Leader first,
                // the current node will degenerate into the Follower role and set the election timer.
                becomeFollower(rpc.getTerm(), null, rpc.getLeaderId(), true);
                //append Log
                return new AppendEntriesResult(rpc.getTerm(), appendEntries(rpc));
            case LEADER:
                //The Leader role receives the AppendEntries message and prints a warning log
                logger.warn("receive append entries rpc from another leader{},ignore", rpc.getLeaderId());
                return new AppendEntriesResult(rpc.getTerm(), false);
            default:
                throw new IllegalStateException("unexpected node ro1e [ " + role.getName() + "]");
        }
    }

    private boolean appendEntries(AppendEntriesRpc rpc) {
        boolean result = context.getLog().appendEntriesFromLeader(rpc.getPreLogIndex(), rpc.getPreLogTerm(), rpc.getEntries());
        if (result){
            context.getLog().advanceCommitIndex(Math.min(rpc.getLeaderCommit(),rpc.getLastEntryIndex()),rpc.getTerm());
        }
        return result;
    }


    @Subscribe
    public void onReceiveAppendEntriesResult(AppendEntriesResultMessage  resultMessage){
        context.getTaskExecutor().submit(()->doProcessAppendEntriesResult(resultMessage));
    }

    private void doProcessAppendEntriesResult(AppendEntriesResultMessage resultMessage) {
        AppendEntriesResult result=resultMessage.get();
        //If the other party's term is larger than your own, it will degenerate into the Follower role.
        if (result.getTerm()>role.getTerm()){
            becomeFollower(result.getTerm(),null,null,true);
            return;
        }
        //check own role
        if (role.getName() !=RoleName.LEADER){
            logger.warn (" receive append entries result from node {} but current node is not leader, ignore" , resultMessage.getSourceNodeId());
        }

        NodeId sourceNodeId = resultMessage.getSourceNodeId();
        GroupMember member = context.getGroup().getMember(sourceNodeId);
        //if not point anyone
        if (member==null) {
            logger.info("unexpected append entries result from node{},node maybe removed",sourceNodeId);
            return;
        }
        AppendEntriesRpc rpc=resultMessage.getRpc();
        if(result.isSuccess()){
            //Reply successful
            //advance the matchIndex and nextIndex
            if (member.advanceReplicatingState(rpc.getLastEntryIndex())){
                //advance the local commitIndex
                context.getLog().advanceCommitIndex(context.getGroup().getMatchIndexOfMajor(),role.getTerm());
            }else {
                //Reply fail
                if (!member.backOffNextIndex()){
                    logger.warn("cannot back off next index more,node {}",sourceNodeId);
                }
            }
        }

    }
}
