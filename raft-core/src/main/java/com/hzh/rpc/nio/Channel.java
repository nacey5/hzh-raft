package com.hzh.rpc.nio;

import com.hzh.domain.message.AppendEntriesResult;
import com.hzh.domain.message.AppendEntriesRpc;
import com.hzh.domain.message.RequestVoteResult;
import com.hzh.domain.message.RequestVoteRpc;

/**
 * @ClassName Channel
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/8 17:24
 * @Version 0.0.1
 **/
public interface Channel {
    //send the requestVote Message
    void writeRequestVoteRpc(RequestVoteRpc rpc);
    //send the requestVote response
    void writeRequestVoteRpc(RequestVoteResult result);
    //send AppendEntries message
    void writeAppendEntriesRpc(AppendEntriesRpc rpc);
    //end AppendEntries response
    void writeAppendEntriesResult(AppendEntriesResult result);
    //close
    void  close();
}


