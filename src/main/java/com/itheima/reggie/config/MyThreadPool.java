package com.itheima.reggie.config;

import java.util.concurrent.*;

/**
 * @author chang zhiwei
 * @date 2024/6/11 15:16
 */
public class MyThreadPool implements Executor {
    private static final int CORE_POOL_SIZE = 10;
    private static final int MAX_POOL_SIZE = 20;
    private static final long KEEP_ALIVE_TIME = 60L;
    private static final int QUEUE_CAPACITY = 100;

    public static ExecutorService createThreadPool() {
        return new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(QUEUE_CAPACITY),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    @Override
    public void execute(Runnable command) {

    }
}
