package com.hzh.domain.log.sequence;

import com.hzh.domain.log.entry.Entry;
import com.hzh.domain.log.entry.sepecific.GeneralEntry;
import com.hzh.domain.log.entry.sepecific.NoOpEntry;

/**
 * @ClassName EntryFactory
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/5 12:44
 * @Version 0.0.1
 **/
public class EntryFactory {
    public Entry create(int kind, int index, int term, byte[] commandBytes) {
        switch (kind) {
            case Entry.KIND_OP_OP:
                return new NoOpEntry(index, term);
            case Entry.KIND_GENERAL:
                return new GeneralEntry(index, term, commandBytes);
            default:
                throw new IllegalArgumentException("unexpected entry kind " + kind);
        }
    }
}
