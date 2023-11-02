package com.hzh.domain.message;

import lombok.Data;

/**
 * @ClassName AppendEntriesResult
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/1 16:26
 * @Version 0.0.1
 **/
@Data
public class AppendEntriesResult {
    private final int term;
    private final boolean success;
    private final String rpcMessageId;

    public AppendEntriesResult(int term, boolean success) {
        this.term = term;
        this.success = success;
        rpcMessageId="";
    }
    public AppendEntriesResult(String rpcMessageId, int term, boolean success) {
        this.rpcMessageId = rpcMessageId;
        this.term = term;
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
