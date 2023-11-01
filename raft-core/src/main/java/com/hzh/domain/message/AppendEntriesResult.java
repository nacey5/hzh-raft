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

    public AppendEntriesResult(int term, boolean success) {
        this.term = term;
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
