package com.hzh.exctutor.sepecific;

import com.hzh.exctutor.TaskExecutor;

import java.util.concurrent.*;

/**
 * @ClassName SingleThreadTaskExecutor
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/1 16:44
 * @Version 0.0.1
 **/
public class SingleThreadTaskExecutor implements TaskExecutor {

    private final ExecutorService executorService;

    public SingleThreadTaskExecutor(ExecutorService executorService) {
        this(Executors.defaultThreadFactory());
    }

    public SingleThreadTaskExecutor(String name){
        this(r->new Thread(r,name));
    }

    public SingleThreadTaskExecutor(ThreadFactory threadFactory) {
        executorService=Executors.newSingleThreadExecutor(threadFactory);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return executorService.submit(task);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return executorService.submit(task);
    }

    @Override
    public void shutdown() throws InterruptedException {
        executorService.shutdown();
        executorService.awaitTermination(1,TimeUnit.SECONDS);
    }
}
