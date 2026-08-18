package com.slotsync.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Async thread pool configuration.
 *
 * <p>Design Decision: A dedicated, named executor is defined rather than relying
 * on Spring's default {@code SimpleAsyncTaskExecutor} (which creates a new thread
 * per task). The named pool provides:
 * <ul>
 *   <li>Bounded concurrency — prevents thread explosion under load</li>
 *   <li>Named threads — appear in logs as {@code slotsync-async-N} for easy tracing</li>
 *   <li>Rejection policy — caller runs if queue is full (graceful degradation)</li>
 * </ul>
 *
 * <p>Used by {@code @Async} methods in the notification pipeline.
 */
@Configuration
public class AsyncConfig {

    /**
     * Primary async executor for notification events and background tasks.
     * The bean name "taskExecutor" is Spring's conventional override hook —
     * annotating a method with @Bean("taskExecutor") replaces the default executor.
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(100);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("slotsync-async-");
        // CallerRunsPolicy: if pool is saturated, execute on the calling thread
        // rather than silently dropping tasks.
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }
}
