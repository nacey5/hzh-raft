package com.hzh.domain.node;


import com.hzh.domain.log.ReplicatingState;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger logger= LoggerFactory.getLogger(NodeGroup.class);

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

    public int getMatchIndexOfMajor() {
       List<NodeMatchIndex> matchIndices=new ArrayList<>();
        for (GroupMember member : memberMap.values()) {
            if (!member.idEquals(selfId)){
                matchIndices.add(new NodeMatchIndex(member.getId(),member.getMatchIndex()));
            }
        }
        int count=matchIndices.size();
        // has not any node
        if (count==0){
            throw new IllegalStateException("standalone or no major node");
        }
        Collections.sort(matchIndices);
        logger.debug("match indices {}",matchIndices);
        //Get the matchIndex at the middle position after sorting
        return matchIndices.get(count/2).getMatchIndex();
    }

    void resetReplicatingStates(int nextLogIndex) {
        for (GroupMember member : memberMap.values()) {
            if (!member.idEquals(selfId)) {
                member.setReplicatingState(new ReplicatingState(nextLogIndex));
            }
        }
    }


    private static class NodeMatchIndex implements Comparable<NodeMatchIndex>{

        private final NodeId nodeId;
        private final int matchIndex;

        private NodeMatchIndex(NodeId nodeId, int matchIndex) {
            this.nodeId = nodeId;
            this.matchIndex = matchIndex;
        }

        int getMatchIndex(){
            return matchIndex;
        }

        @Override
        public int compareTo(NodeMatchIndex o) {
            return Integer.compare(matchIndex, o.matchIndex);
        }

        @Override
        public String toString() {
            return "NodeMatchIndex{" +
                    "nodeId=" + nodeId +
                    ", matchIndex=" + matchIndex +
                    '}';
        }
    }
}
