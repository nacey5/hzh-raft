package com.hzh.domain.log.task;

import com.hzh.domain.timer.sepecific.NullScheduledFuture;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName LogReplicationTask
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/1 15:57
 * @Version 0.0.1
 **/
public class LogReplicationTask {
    private final ScheduledFuture<?> scheduledFuture;
    public static final LogReplicationTask NONE = new LogReplicationTask(new NullScheduledFuture());

    //construct

    public LogReplicationTask(ScheduledFuture<?> scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }

    public void cancel() {
        this.scheduledFuture.cancel(false);
    }

    @Override
    public String toString() {
        return " LogReplicationTask{delay- " +
                scheduledFuture.getDelay(TimeUnit.MILLISECONDS) + " }";
    }
}
