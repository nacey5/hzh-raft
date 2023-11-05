package com.hzh.domain.log.entry.sepecific;

import com.hzh.domain.log.entry.AbstractEntry;

/**
 * @ClassName GeneralEntry
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/4 16:21
 * @Version 0.0.1
 **/
public class GeneralEntry extends AbstractEntry {

    private final byte[] commandBytes;
    public GeneralEntry(int kind, int index, int term,byte[] commandBytes) {
        super(KIND_GENERAL, index, term);
        this.commandBytes=commandBytes;
    }

    public GeneralEntry(int index, int term,byte[] commandBytes) {
        super(KIND_GENERAL, index, term);
        this.commandBytes=commandBytes;
    }

    public byte[] getCommandBytes(){
        return this.commandBytes;
    }

    @Override
    public String toString() {
        return "GeneralEntry{" +
                "index=" + index +
                ", term=" + term +
                '}';
    }
}
