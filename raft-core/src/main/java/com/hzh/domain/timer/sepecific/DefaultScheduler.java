package com.hzh.domain.timer.sepecific;

import com.hzh.domain.log.task.LogReplicationTask;
import com.hzh.domain.timer.ElectionTimeout;
import com.hzh.domain.timer.Scheduler;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName DefaultScheduler
 * @Description the config for the Scheduler
 * @Author DaHuangGo
 * @Date 2023/11/1 15:58
 * @Version 0.0.1
 **/
public class DefaultScheduler implements Scheduler {
    private final int minElectionTimeout;

    private final int maxElectionTimeout;
    //the first delay time for the coping log
    private final int logReplicationDelay;

    private final int logReplicationInterval;

    private final Random electionTimeoutRandom;

    private final ScheduledExecutorService scheduledExecutorService;

    public DefaultScheduler(int minElectionTimeout, int maxElectionTimeout,
                            int logReplicationDelay, int logReplicationInterval) {
        //Determine whether the parameters are valid
        if (minElectionTimeout <= 0 || maxElectionTimeout <= 0 || minElectionTimeout > maxElectionTimeout) {
            throw new IllegalArgumentException("election timeout should not be 0 or min>max");
        }
        //Initial log replication delay and log replication interval
        if (logReplicationDelay < 0 || logReplicationInterval <= 0) {
            throw new IllegalArgumentException("log replication delay < 0 or log replication interval<=0");
        }
        this.minElectionTimeout = minElectionTimeout;
        this.maxElectionTimeout = maxElectionTimeout;
        this.logReplicationDelay = logReplicationDelay;
        this.logReplicationInterval = logReplicationInterval;
        electionTimeoutRandom = new Random();
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "election-timeout-scheduler"));
    }

    /**
     * Create a log replication schedule
     * @param task
     * @return
     */
    @Override
    public LogReplicationTask scheduleLogReplicationTask(Runnable task) {
        ScheduledFuture<?> scheduledFuture = this.scheduledExecutorService.scheduleAtFixedRate(
                task, logReplicationDelay, logReplicationInterval, TimeUnit.MILLISECONDS
        );
        return new LogReplicationTask(scheduledFuture);
    }
    /**
     * According to the requirements of the Raft algorithm, in order to reduce the impact of splìt vo constant,
     * a timeout is randomly selected within the election timeout interval.
     * time, rather than a fixed time when the election begins.
     * @param task
     * @return
     */
    @Override
    public ElectionTimeout scheduleElectionTimeout(Runnable task) {
        // random timeout
        int timeout=electionTimeoutRandom.nextInt(maxElectionTimeout-minElectionTimeout)+minElectionTimeout;
        ScheduledFuture<?> scheduledFuture = scheduledExecutorService.schedule(task, timeout, TimeUnit.MILLISECONDS);
        return new ElectionTimeout(scheduledFuture);
    }

    @Override
    public void stop() throws InterruptedException {

    }
}
