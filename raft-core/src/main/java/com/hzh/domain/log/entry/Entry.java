package com.hzh.domain.log.entry;

/**
 * @ClassName Entry
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/1 16:24
 * @Version 0.0.1
 **/
public interface Entry {
    // the type of log
    int KIND_OP_OP=0;
    int KIND_GENERAL=1;

    int getKind();
    int getIndex();
    int getTerm();

    EntryMeta getMeta();

    // get the balance of the log
    byte[] getCommandBytes();
}
