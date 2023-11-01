package com.hzh.domain.node;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName NodeId
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/1 15:25
 * @Version 0.0.1
 **/
@Data
public class NodeId implements Serializable {
    private final String value;

    public NodeId(String value) {
        this.value = value;
    }

    public static NodeId of(String value) {
        return new NodeId(value);
    }
}
