package com.hzh.rpc;

import com.google.common.base.Preconditions;
import lombok.Data;

import javax.annotation.Nonnull;

/**
 * @ClassName Addresss
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/2 11:16
 * @Version 0.0.1
 **/
@Data
public class Address {

    private final String host;
    private final int port;

    public Address(@Nonnull String host, int port) {
        Preconditions.checkNotNull(host);
        this.host = host;
        this.port = port;
    }
}
