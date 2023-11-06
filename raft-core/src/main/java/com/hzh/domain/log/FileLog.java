package com.hzh.domain.log;

import java.io.File;

/**
 * @ClassName FileLog
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/6 12:59
 * @Version 0.0.1
 **/
public class FileLog extends AbstractLog{
    private final RootDir rootDir;

    public FileLog(File baseDir){
        rootDir=new RootDir(baseDir);
        // get the newest log term
        LogGeneration latestGeneration =rootDir.getLatestGeneraction();
        if (latestGeneration==null){
            // the log exists
            entrySequence=new FileEntrySequence(latestGeneration,latestGeneration.getLastIncludedIndex());
        }else {
            // the log not exists
            LogGeneration firstGeneration =rootDir.createFirstGeneration();
            entrySequence=new FileEntrySequence(firstGeneration,1);
        }
    }

}
