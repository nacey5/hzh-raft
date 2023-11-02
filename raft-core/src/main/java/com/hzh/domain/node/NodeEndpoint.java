package com.hzh.domain.node;

import com.google.common.base.Preconditions;
import com.hzh.rpc.Address;
import lombok.Data;

import javax.annotation.Nonnull;

/**
 * @ClassName NodeEndpoint
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/1 20:27
 * @Version 0.0.1
 **/

@Data
public class NodeEndpoint {
    private final NodeId id;
    private final Address address;

    public NodeEndpoint(@Nonnull String id, @Nonnull String host, int port) {
        this(new NodeId(id), new Address(host, port));
    }

    public NodeEndpoint(@Nonnull NodeId id, @Nonnull Address address) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(address);
        this.id = id;
        this.address = address;
    }
}
