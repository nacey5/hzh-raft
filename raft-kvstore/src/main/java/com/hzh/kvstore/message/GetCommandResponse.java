package com.hzh.kvstore.message;

import java.util.Arrays;

/**
 * @ClassName GetCommandResponse
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/9 23:08
 * @Version 0.0.1
 **/
public class GetCommandResponse {
    private final boolean found;
    private final byte[] value;

    public GetCommandResponse(byte[] value) {
        this(value != null, value);
    }

    public GetCommandResponse(boolean found, byte[] value) {
        this.found = found;
        this.value = value;
    }

    public boolean isFound() {
        return found;
    }

    public byte[] getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "GetCommandResponse{" +
                "found=" + found +
                ", value=" + Arrays.toString(value) +
                '}';
    }
}
