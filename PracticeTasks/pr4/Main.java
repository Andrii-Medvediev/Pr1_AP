package pr4;

import java.util.Arrays;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) {
        Executor executor = Executors.newFixedThreadPool(4);

        System.out.println("Програма стартувала в потоці: " + Thread.currentThread().getName());

        // Використання runAsync() для повідомлення про старт задач
        CompletableFuture<Void> startTask = CompletableFuture.runAsync(() -> {
            System.out.println("Фонові асинхронні задачі стартують... (потік " + Thread.currentThread().getName() + ")");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, executor);

        // Використання supplyAsync() для генерації даних
        CompletableFuture<int[]> dataTask = CompletableFuture.supplyAsync(() -> {
            System.out.println("Генерація даних... (потік " + Thread.currentThread().getName() + ")");
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Генерація масиву з 10 випадковими числами від 1 до 100
            return new java.util.Random().ints(10, 1, 101).toArray();
        }, executor);

        // Використання thenApplyAsync() для модифікації даних
        CompletableFuture<int[]> processedDataTask = dataTask.thenApplyAsync(data -> {
            System.out.println("Модифікація даних... (потік " + Thread.currentThread().getName() + ")");
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return Arrays.stream(data).map(i -> i + 1).toArray();
        }, executor);

        // Використання thenAcceptAsync() для виводу результатів
        CompletableFuture<Void> printTask = processedDataTask.thenAcceptAsync(data -> {
            System.out.println("Оброблені дані: " + Arrays.toString(data) + ". (потік " + Thread.currentThread().getName() + ")");
        }, executor);

        // Використання thenRunAsync() для повідомлення про завершення всіх задач
        CompletableFuture<Void> finalTask = printTask.thenRunAsync(() -> {
            System.out.println("Усі асинхронні задачі завершено. (потік " + Thread.currentThread().getName() + ")");
        }, executor);

        // Дочекатися завершення всіх задач
        CompletableFuture.allOf(startTask, finalTask).join();

        System.out.println("Програма завершена в потоці: " + Thread.currentThread().getName());

        // Завершення Executor
        ((ExecutorService) executor).shutdown();
    }
}