package com.ruoyi.framework.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 异步配置
 *
 * @author Lion Li
 */
@EnableAsync(proxyTargetClass = true)
@Configuration
@Slf4j
public class AsyncConfig extends AsyncConfigurerSupport {

    @Autowired
    @Qualifier("scheduledExecutorService")
    private ScheduledExecutorService scheduledExecutorService;

    // @Bean
    // public ThreadPoolTaskScheduler scheduledExecutorService() {
    //     ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    //     scheduler.setPoolSize(20);
    //     scheduler.setRemoveOnCancelPolicy(true);
    //     scheduler.setThreadNamePrefix("async-scheduled-task-");
    //     return scheduler;
    // }

    /**
     * 自定义 @Async 注解使用系统线程池
     */
    @Override
    public Executor getAsyncExecutor() {
        return scheduledExecutorService;
    }

    /**
     * 异步执行异常处理
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, objects) -> {
            log.error("异步执行方法出错：{}", method, throwable);
            // StringBuilder sb = new StringBuilder();
            // sb.append("Exception message - ").append(throwable.getMessage())
            //     .append(", Method name - ").append(method.getName());
            // if (ArrayUtil.isNotEmpty(objects)) {
            //     sb.append(", Parameter value - ").append(Arrays.toString(objects));
            // }
            // throw new ServiceException(sb.toString());
        };
    }


}
