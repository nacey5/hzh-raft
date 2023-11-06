package com.hzh.domain.log.sequence;

import com.hzh.domain.log.entry.EntryMeta;
import lombok.Data;

/**
 * @ClassName EntryIndexItem
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/5 13:51
 * @Version 0.0.1
 **/
@Data
public class EntryIndexItem {
    int index;
    long offset;
    int kind;
    int term;

    public EntryIndexItem(int index, long offset, int kind, int term) {
        this.index = index;
        this.offset = offset;
        this.kind = kind;
        this.term = term;
    }

    public EntryMeta toEntryMeta() {
        return new EntryMeta(kind, index, term);
    }

    @Override
    public String toString() {
        return "EntryIndexItem{" +
                "index=" + index +
                ", offset=" + offset +
                ", kind=" + kind +
                ", term=" + term +
                '}';
    }
}
