package com.ruoyi.schedule.util;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class WritePool {
    @Bean("writeThreadPool")
    public ExecutorService writeThreadPool() {
        return new ThreadPoolExecutor(
            50, 50,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(60)
        );
    }
}
