package pr4.task2;

import java.util.concurrent.*;
import java.util.Random;
import java.util.Arrays;


public class Main {
    public static void main(String[] args) {
        System.out.println("Програма стартувала... \t\t\t\t\t(потік " + Thread.currentThread().getName() + ")\n");

        long startTime = System.currentTimeMillis();

        // Створюємо ExecutorService з 4 потоками
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        // Асинхронно виконуємо логування
        CompletableFuture<Void> logTask = CompletableFuture.runAsync(() -> {
            long taskStart = System.currentTimeMillis();
            System.out.println("Виконується логування перед обчисленнями... \t\t(потік " + Thread.currentThread().getName() + ")");
            try {
                TimeUnit.MILLISECONDS.sleep(100); // Симуляція логування
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            printExecutionTime(taskStart, "Логування");
        }, executorService);

        // Після завершення логування генеруємо послідовність чисел
        CompletableFuture<double[]> generateSequence = logTask.thenCompose(_ -> CompletableFuture.supplyAsync(() -> {
            long taskStart = System.currentTimeMillis();
            System.out.println("Генерація послідовності чисел... \t\t\t(потік " + Thread.currentThread().getName() + ")");
            Random random = new Random();
            double[] sequence = new double[20];
            for (int i = 0; i < sequence.length; i++) {
                sequence[i] = Math.floor(random.nextDouble() * 10000) / 100.0;
            }
            System.out.println("Згенерована послідовність: " + Arrays.toString(sequence));
            printExecutionTime(taskStart, "Генерація послідовності");
            return sequence;
        }, executorService));

        // Асинхронно обчислюємо модулі різниць сусідніх елементів
        CompletableFuture<double[]> calculateDifferences = generateSequence.thenApplyAsync(sequence -> {
            long taskStart = System.currentTimeMillis();
            System.out.println("Обчислення модулів різниць... \t\t\t\t(потік " + Thread.currentThread().getName() + ")");
            double[] differences = new double[sequence.length - 1];
            for (int i = 0; i < sequence.length - 1; i++) {
                differences[i] = Math.round(Math.abs(sequence[i] - sequence[i + 1]) * 100.0) / 100.0;
            }
            System.out.println("Модулі різниць: \t   " + Arrays.toString(differences));
            printExecutionTime(taskStart, "Обчислення модулів різниць");
            return differences;
        }, executorService);

        // Асинхронно знаходимо максимальний елемент
        CompletableFuture<Double> findMax = calculateDifferences.thenApplyAsync(differences -> {
            long taskStart = System.currentTimeMillis();
            System.out.println("Пошук максимального значення... \t\t\t(потік " + Thread.currentThread().getName() + ")");
            double max = differences[0];
            for (double diff: differences) {
                if (diff > max) {
                    max = diff;
                }
            }
            System.out.println("Максимальне значення: " + max);
            printExecutionTime(taskStart, "Пошук максимального значення");
            return max;
        }, executorService);

        // Асинхронно виводимо результат
        CompletableFuture<Void> printResult = findMax.thenAcceptAsync(max -> {
            long taskStart = System.currentTimeMillis();
            System.out.println("\nРезультат: Максимальне значення модулів різниць = " + max + " (потік " + Thread.currentThread().getName() + ")");
            printExecutionTime(taskStart, "Вивід результату");
        }, executorService);

        // Після завершення всіх задач виводимо загальний час виконання
        CompletableFuture<Void> finalTask = printResult.thenRunAsync(() -> {
            long endTime = System.currentTimeMillis();
            System.out.println("\nУсі задачі завершено. Загальний час виконання: " + (endTime - startTime) + " мс \t(потік " + Thread.currentThread().getName() + ")");
        }, executorService);

        finalTask.join();

        System.out.println("Програма завершена. \t\t\t\t\t(потік " + Thread.currentThread().getName() + ")");

        executorService.shutdown();
    }

    // Метод для виводу часу виконання задачі
    private static void printExecutionTime(long start, String taskName) {
        long end = System.currentTimeMillis();
        System.out.println(taskName + " виконано за: " + (end - start) + " мс");
    }
}
