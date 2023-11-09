package com.hzh.domain.message;

import lombok.Data;

/**
 * @ClassName RequestVoteResult
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/1 16:21
 * @Version 0.0.1
 **/

@Data
public class RequestVoteResult {
    private final int term;
    // is voted or not
    // if is true,Representatives have received votes
    private final boolean voteGranted;

    public RequestVoteResult(int term, boolean voteGranted) {
        this.term = term;
        this.voteGranted = voteGranted;
    }

    public boolean isVoteGranted() {
        return voteGranted;
    }
}
