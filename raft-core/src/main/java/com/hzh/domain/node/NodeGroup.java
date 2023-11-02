package com.hzh.domain.node;


import lombok.Data;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName NodeGroup
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/1 16:35
 * @Version 0.0.1
 **/
@Data
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

    NodeGroup(Collection<NodeEndpoint> endpoints, NodeId selfId) {
        this.memberMap = buildMemberMap(endpoints);
        this.selfId = selfId;
    }

    /**
     * build memberMap from endpoints
     * @param endpoints
     * @return
     */
    private Map<NodeId, GroupMember> buildMemberMap(Collection<NodeEndpoint> endpoints) {
        Map<NodeId, GroupMember> map = new HashMap<>();
        for (NodeEndpoint endpoint : endpoints) {
            map.put(endpoint.getId(), new GroupMember(endpoint));
        }
        if (map.isEmpty()) {
            throw new IllegalArgumentException("endpoints is empty");
        }
        return map;
    }

    @Nullable
    public GroupMember getMember(NodeId id){
        return memberMap.get(id);
    }

    public int getCount(){
        return memberMap.values().size();
    }

    public Collection<GroupMember> listReplicationTargets() {
        return memberMap.values().stream().filter(m->!m.idEquals(selfId)).collect(Collectors.toList());
    }
}
