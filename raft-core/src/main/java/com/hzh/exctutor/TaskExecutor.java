package com.hzh.exctutor;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * usage:
 * context.taskExecutor().submit(() -> {
 *     do you biz
 * });
 * @ClassName TaskExecutor
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/1 16:41
 * @Version 0.0.1
 **/
public interface TaskExecutor {
    //submit task
    Future<?> submit(Runnable task);

    //submit task with result
    <T> Future<T> submit(Callable<T> task);

    //close the taskExecutor
    void shutdown() throws InterruptedException;
}
