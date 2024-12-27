package com.task.pr6;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class AsyncConfiguration {

    // Кастомний Scheduler для запланованих задач
    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(3); // Задаємо пул потоків розміром 3
        scheduler.setThreadNamePrefix("ScheduledTask-");
        scheduler.initialize();
        return scheduler;
    }
}
