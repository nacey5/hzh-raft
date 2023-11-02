package com.hzh.domain.timer.sepecific;

import com.hzh.domain.log.task.LogReplicationTask;
import com.hzh.domain.timer.ElectionTimeout;
import com.hzh.domain.timer.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

/**
 * @ClassName NullScheduler
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/2 14:26
 * @Version 0.0.1
 **/
public class NullScheduler implements Scheduler {
    private static final Logger logger = LoggerFactory.getLogger(NullScheduler.class);

    @Override
    @Nonnull
    public LogReplicationTask scheduleLogReplicationTask(Runnable task) {
        logger.debug("schedule log replication task");
        return LogReplicationTask.NONE;
    }

    @Override
    @Nonnull
    public ElectionTimeout scheduleElectionTimeout(Runnable task) {
        logger.debug("schedule election timeout");
        return ElectionTimeout.NONE;
    }

    @Override
    public void stop() throws InterruptedException {

    }
}
