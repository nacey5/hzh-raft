package com.hzh.domain.message;

import com.hzh.domain.log.entry.Entry;
import com.hzh.domain.node.NodeId;
import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * @ClassName AppendEntriesRpc
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/1 16:22
 * @Version 0.0.1
 **/

@Data
public class AppendEntriesRpc {
    private int term;
    private NodeId leaderId;
    private int preLogIndex=0;
    private int preLogTerm;
    private List<Entry> entries= Collections.emptyList();
    // the commit index for the leader
    private int leaderCommit;
}
