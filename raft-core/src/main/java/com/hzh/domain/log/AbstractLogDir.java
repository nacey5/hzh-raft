package com.hzh.domain.log;

import java.io.File;

/**
 * @ClassName AbstractLogDir
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/6 13:11
 * @Version 0.0.1
 **/
public class AbstractLogDir implements LogDir{

    final File dir;

    AbstractLogDir(File dir) {
        this.dir = dir;
    }
    @Override
    public void initialize() {

    }

    @Override
    public boolean exists() {
        return false;
    }

    @Override
    public File getEntriesFile() {
        return null;
    }

    @Override
    public File getEntryOffsetIndexFile() {
        return null;
    }

    @Override
    public File get() {
        return null;
    }

    @Override
    public boolean renameTo(LogDir logDir) {
        return false;
    }
}
