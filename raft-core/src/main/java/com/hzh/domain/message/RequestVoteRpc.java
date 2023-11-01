package com.hzh.domain.message;

import com.hzh.domain.node.NodeId;
import lombok.Data;

/**
 * @ClassName RequestVoteRpc
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/1 16:19
 * @Version 0.0.1
 **/
@Data
public class RequestVoteRpc {

    private int term;
    private NodeId candidateId;
    //The index of the candidate’s last entry date
    private int lastLogIndex = 0;
    private int lastLogTerm = 0;

}
