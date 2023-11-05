package com.hzh.domain.log.entry;

/**
 * @ClassName AbstractEntry
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/4 16:11
 * @Version 0.0.1
 * the abstract clz is for the op the log
 **/
public class AbstractEntry implements Entry {

    private final int kind;
    protected final int index;
    protected final int term;

    public AbstractEntry(int kind, int index, int term) {
        this.kind = kind;
        this.index = index;
        this.term = term;
    }

    @Override
    public int getKind() {
        return this.kind;
    }

    @Override
    public int getIndex() {
        return this.index;
    }

    @Override
    public int getTerm() {
        return this.term;
    }

    @Override
    public EntryMeta getMeta() {
        return new EntryMeta(kind,index,term);
    }

    @Override
    public byte[] getCommandBytes() {
        return new byte[0];
    }
}
