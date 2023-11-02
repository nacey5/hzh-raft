package com.hzh.domain.timer;

import com.hzh.domain.timer.sepecific.NullScheduledFuture;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName ElectionTimeout
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/1 15:32
 * @Version 0.0.1
 **/
public class ElectionTimeout {
    private final ScheduledFuture<?> scheduledFuture;
    public static final ElectionTimeout NONE = new ElectionTimeout(new NullScheduledFuture());
    //construct
    public ElectionTimeout(ScheduledFuture<?> scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }

    // cancel the election timeout
    public void cancel() {
        this.scheduledFuture.cancel(false);
    }

    @Override
    public String toString() {
        //Election timeout canceled
        if (this.scheduledFuture.isCancelled()) {
            return "ElectionTimeout(state=cancelled) ";
        }
        //Election timeout has executed
        if (this.scheduledFuture.isDone()) {
            return "ElectionTimeout(state=done)";
        }
        ///The election timeout has not been executed yet, after how many milliseconds it will be executed
        return "ElectionTimeout(delay=" + this.scheduledFuture.getDelay(TimeUnit.MILLISECONDS) + "ms)";
    }
}
