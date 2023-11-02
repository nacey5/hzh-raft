package com.hzh.domain.role.specific;

import com.hzh.domain.node.NodeId;
import com.hzh.domain.role.AbstractNodeRole;
import com.hzh.domain.role.enums.RoleName;
import com.hzh.domain.log.task.LogReplicationTask;

/**
 *
 * 后面将使用状态机进行状态转换，因为只有三个角色，状态机的优势没办法很好的体现出来
 * @ClassName LeaderNodeRole
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/1 15:51
 * @Version 0.0.1
 **/
public class LeaderNodeRole extends AbstractNodeRole {

    //日志定时复制器
    private final LogReplicationTask logReplicationTask;

    public LeaderNodeRole(int term, LogReplicationTask logReplicationTask) {
        super(RoleName.LEADER, term);
        this.logReplicationTask = logReplicationTask;
    }

    @Override
    public void cancelTimeoutOrTask() {
        logReplicationTask.cancel();
    }

    @Override
    public NodeId getLeaderId(NodeId selfId) {
        return selfId;
    }

    @Override
    public String toString() {
        return "LeaderNodeRole{" +
                "logReplicationTask=" + logReplicationTask +
                ", term=" + term +
                '}';
    }
}
