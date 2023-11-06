package com.hzh.domain.log.entry;

import lombok.Data;

/**
 * @ClassName EntryMeta
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/4 16:11
 * @Version 0.0.1
 **/
@Data
public class EntryMeta {
    private int index;
    private int term;

    private int kind;
    public EntryMeta(int kind, int index, int term) {
        this.index=index;
        this.kind=kind;
        this.term=term;
    }
}
