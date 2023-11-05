package com.hzh.domain.log.sequence;

/**
 * @ClassName EntryIndexItem
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/5 13:51
 * @Version 0.0.1
 **/
public class EntryIndexItem {
    int i;
    long offset;
    int kind;
    int term;

    public EntryIndexItem(int i, long offset, int kind, int term) {
        this.i = i;
        this.offset = offset;
        this.kind = kind;
        this.term = term;
    }

    @Override
    public String toString() {
        return "EntryIndexItem{" +
                "i=" + i +
                ", offset=" + offset +
                ", kind=" + kind +
                ", term=" + term +
                '}';
    }
}
