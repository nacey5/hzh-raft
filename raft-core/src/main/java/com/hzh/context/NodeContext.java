package com.hzh.context;

import com.google.common.eventbus.EventBus;
import com.hzh.config.NodeConfig;
import com.hzh.domain.node.*;
import com.hzh.domain.timer.Scheduler;
import com.hzh.exctutor.TaskExecutor;
import com.hzh.rpc.Connector;
import lombok.Data;

/**
 * @ClassName NodeContext
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/1 16:28
 * @Version 0.0.1
 **/

@Data
public class NodeContext {
    private NodeId selfId;
    // member List
    private NodeGroup group;

    //private Log log;

    //RPC components
    private Connector connector;
    //timer components
    private Scheduler scheduler;
    private EventBus eventBus;
    // the main thread executor
    private TaskExecutor taskExecutor;
    //Partial role status data storage
    private NodeStore store;

    private NodeConfig config;


    public GroupMember findMember(NodeId nodeId){
        GroupMember member = group.getMember(nodeId);
        if (member == null) {
            throw new IllegalArgumentException("no such node " + nodeId);
        }
        return member;
    }
}
