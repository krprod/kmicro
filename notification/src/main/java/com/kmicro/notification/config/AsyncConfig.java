package com.kmicro.notification.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@EnableRetry
public class AsyncConfig {
    @Bean(name = "emailExecutor")
    public Executor emailExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // Number of threads always kept alive
        executor.setCorePoolSize(5);

        // Maximum number of threads allowed if queue is full
        executor.setMaxPoolSize(10);

        // Number of emails that can wait in line before maxPoolSize kicks in
        executor.setQueueCapacity(100);

        // Crucial for monitoring and debugging logs
        executor.setThreadNamePrefix("EmailWorker-");

        // Ensure all emails are sent before the app shuts down
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);

        executor.initialize();
        return executor;
    }


}
