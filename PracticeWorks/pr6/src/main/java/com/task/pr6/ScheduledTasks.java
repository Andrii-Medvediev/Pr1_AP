package com.task.pr6;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ScheduledTasks {

    // Метод, який виконується щодня о 10:00
    @Scheduled(cron = "10 * * * * *")
    public void dailyTask() {
        System.out.println("Щоденна задача виконується о 10:00. Поточний час: " + LocalDateTime.now()
                + " (потік " + Thread.currentThread().getName() + ")");
    }

    // Метод, який виконується кожні 3 секунди з початковою затримкою 2 секунди
    @Scheduled(initialDelay = 2000, fixedRate = 3000)
    public void periodicTask() {
        System.out.println("Запланована задача кожні 3 секунди (затримка 2 с). Поточний час: " + LocalDateTime.now()
                + " (потік " + Thread.currentThread().getName() + ")");
    }
}
