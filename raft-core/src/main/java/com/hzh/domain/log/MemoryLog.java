package com.hzh.domain.log;

import com.hzh.domain.log.sequence.MemoryEntrySequence;

/**
 * @ClassName MemoryLog
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/6 12:43
 * @Version 0.0.1
 **/
public class MemoryLog extends AbstractLog{
    public MemoryLog() {
        this(new MemoryEntrySequence());
    }

    public MemoryLog(MemoryEntrySequence entrySequence) {
        this.entrySequence=entrySequence;
    }
}
