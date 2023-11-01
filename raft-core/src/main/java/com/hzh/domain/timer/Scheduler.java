package com.hzh.domain.timer;

import com.hzh.domain.log.task.LogReplicationTask;

/**
 * @ClassName Scheduler
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/1 15:55
 * @Version 0.0.1
 **/
public interface Scheduler {

    //create the task for the log coping
    LogReplicationTask scheduleLogReplicationTask(Runnable task);

    ElectionTimeout scheduleElectionTimeout(Runnable task);

    void stop() throws InterruptedException;
}
