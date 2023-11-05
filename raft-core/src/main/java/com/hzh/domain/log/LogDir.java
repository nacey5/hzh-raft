package com.hzh.domain.log;

import java.io.File;

/**
 * @ClassName LogDir
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/5 17:02
 * @Version 0.0.1
 **/
public interface LogDir {
    void initialize();

    boolean exists();

    File getEntriesFile();

    File getEntryOffsetIndexFile();

    File get();

    boolean renameTo(LogDir logDir);
}
