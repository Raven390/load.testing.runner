package ru.develonica.load.testing.runner.service.load;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadPoolFactory {

    public static ThreadPoolExecutor newThreadPool(Integer poolSize) {
        return (ThreadPoolExecutor) Executors.newFixedThreadPool(poolSize);
    }
}
