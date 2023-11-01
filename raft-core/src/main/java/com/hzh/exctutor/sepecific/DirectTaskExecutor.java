package com.hzh.exctutor.sepecific;

import com.hzh.exctutor.TaskExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * @ClassName DirectTaskExecutor
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/1 16:51
 * @Version 0.0.1
 **/
public class DirectTaskExecutor implements TaskExecutor {
    @Override
    public Future<?> submit(Runnable task) {
        FutureTask<?> futureTask = new FutureTask<>(task, null);
        futureTask.run();
        return futureTask;
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        FutureTask<T> futureTask = new FutureTask<>(task);
        futureTask.run();
        return futureTask;
    }

    @Override
    public void shutdown() throws InterruptedException {

    }
}
