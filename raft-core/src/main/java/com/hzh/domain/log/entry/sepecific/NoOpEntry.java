package com.hzh.domain.log.entry.sepecific;

import com.hzh.domain.log.entry.AbstractEntry;
import com.hzh.domain.log.entry.Entry;

/**
 * @ClassName NoOpEntry
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/4 16:25
 * @Version 0.0.1
 **/
public class NoOpEntry extends AbstractEntry {
    public NoOpEntry(int kind, int index, int term) {
        super(kind, index, term);
    }

    public NoOpEntry(int index, int term) {
        super(Entry.KIND_OP_OP, index, term);
    }

    public byte[] getCommandBytes() {
        return new byte[0];
    }

    @Override
    public String toString() {
        return "NoOpEntry{" +
                "index=" + index +
                ",term=" + term +
                "}";
    }
}
