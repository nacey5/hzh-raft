package com.hzh.domain.node;


import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName NodeGroup
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/1 16:35
 * @Version 0.0.1
 **/
public class NodeGroup {
    private final NodeId selfId;
    private Map<NodeId,GroupMember> memberMap;

    public Collection<NodeEndpoint> listEndpointExceptSelf() {
        Set<NodeEndpoint> endpoints=new HashSet<>();
        for (GroupMember member : memberMap.values()) {
            //Determine whether it is the current node
            if (!member.getId().equals(selfId)){
                endpoints.add(member.getEndpoint());
            }
        }
        return endpoints;
    }
}
