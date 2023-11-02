package com.hzh.domain.role;

import com.hzh.domain.node.NodeId;
import com.hzh.domain.role.enums.RoleName;
import lombok.Data;

/**
 * @ClassName AbstractNodeRole
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/1 15:19
 * @Version 0.0.1
 **/
@Data
public abstract class AbstractNodeRole {
    private final RoleName name;
    protected final int term;

    protected AbstractNodeRole(RoleName name,int term) {
        this.name=name;
        this.term=term;
    }

    public abstract void cancelTimeoutOrTask();

    public abstract NodeId getLeaderId(NodeId selfId);


}
