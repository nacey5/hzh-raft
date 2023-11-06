package com.hzh.domain.node;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;
import com.hzh.config.NodeConfig;
import com.hzh.context.NodeContext;
import com.hzh.domain.log.Log;
import com.hzh.domain.log.MemoryLog;
import com.hzh.domain.node.memory.MemoryNodeStore;
import com.hzh.domain.timer.Scheduler;
import com.hzh.domain.timer.sepecific.DefaultScheduler;
import com.hzh.exctutor.TaskExecutor;
import com.hzh.exctutor.sepecific.SingleThreadTaskExecutor;
import com.hzh.rpc.Connector;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;

/**
 * @ClassName NodeBuilder
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/2 14:14
 * @Version 0.0.1
 **/
public class NodeBuilder {
    // the members
    private final NodeGroup group;
    private final NodeId selfId;
    private final EventBus eventBus;
    private Scheduler scheduler=null;
    private Connector connector=null;
    private TaskExecutor taskExecutor=null;

    private NodeStore store = null;

    private Log log;

    /**
     * Node configuration.
     */
    private NodeConfig config = new NodeConfig();

    //single node build
    public NodeBuilder(NodeEndpoint endpoint){
        this(Collections.singleton(endpoint),endpoint.getId());
    }

    public NodeBuilder(Collection<NodeEndpoint> endpoints, NodeId selfId) {
        this.group = new NodeGroup(endpoints,selfId);
        this.selfId = selfId;
        this.eventBus = new EventBus(selfId.getValue());
    }

    public NodeBuilder setConnector(Connector connector) {
        this.connector = connector;
        return this;
    }

    public NodeBuilder setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
        return this;
    }

    public NodeBuilder setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
        return this;
    }

    public NodeBuilder setConfig(@Nonnull NodeConfig config) {
        Preconditions.checkNotNull(config);
        this.config = config;
        return this;
    }

    public Node build(){
        return new NodeImpl(buildContext());
    }

    private NodeContext buildContext() {
        NodeContext context=new NodeContext();
        context.setGroup(group);
        context.setSelfId(selfId);
        context.setEventBus(eventBus);
        context.setConfig(config);
        //todo replace to the nodeConfig
        context.setScheduler(scheduler!=null?scheduler:new DefaultScheduler(config));
        context.setConnector(connector);
        context.setStore(store != null ? store : new MemoryNodeStore());
        context.setTaskExecutor(taskExecutor!=null?taskExecutor:new SingleThreadTaskExecutor("node"));
        context.setLog(log!=null?log:new MemoryLog());
        return context;
    }
}
