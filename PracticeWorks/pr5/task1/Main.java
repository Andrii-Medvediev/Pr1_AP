package pr5.task1;

import java.util.concurrent.*;
import java.util.Arrays;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        System.out.println("Програма стартувала... \t\t(потік " + Thread.currentThread().getName() + ")\n");

        // Створюємо пул з 4 потоками
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        // Асинхронно генеруємо масив
        CompletableFuture<double[]> generateArrayTask = CompletableFuture.supplyAsync(() -> {
            System.out.println("Генерація масиву чисел... \t(потік " + Thread.currentThread().getName() + ")");
            Random random = new Random();
            double[] array = new double[20];
            for (int i = 0; i < array.length; i++) {
                array[i] = Math.round((random.nextDouble() * 100) * 100.0) / 100.0;
            }
            return array;
        }, executorService);

        // Виводимо масив після його генерації
        CompletableFuture<Void> printArrayTask = generateArrayTask.thenAcceptAsync(array -> {
            System.out.println("Згенерований масив: " + Arrays.toString(array) + " \n\t\t\t\t(потік " + Thread.currentThread().getName() + ")");
        }, executorService);

        // Асинхронно виконуємо 4 функції після завершення генерації масиву
        CompletableFuture<Void> calculationsTask = printArrayTask.thenCompose(_ -> generateArrayTask.thenCompose(array -> {
            // Додавання
            CompletableFuture<double[]> additionTask = CompletableFuture.supplyAsync(() -> {
                System.out.println("Додавання елементів... \t\t(потік " + Thread.currentThread().getName() + ")");
                double[] result = new double[array.length - 1];
                for (int i = 0; i < array.length - 1; i++) {
                    result[i] = Math.round((array[i] + array[i + 1]) * 100.0) / 100.0;
                }
                return result;
            }, executorService);

            // Віднімання
            CompletableFuture<double[]> subtractionTask = CompletableFuture.supplyAsync(() -> {
                System.out.println("Віднімання елементів... \t(потік " + Thread.currentThread().getName() + ")");
                double[] result = new double[array.length - 1];
                for (int i = 0; i < array.length - 1; i++) {
                    result[i] = Math.round((array[i] - array[i + 1]) * 100.0) / 100.0;
                }
                return result;
            }, executorService);

            // Множення
            CompletableFuture<double[]> multiplicationTask = CompletableFuture.supplyAsync(() -> {
                System.out.println("Множення елементів... \t\t(потік " + Thread.currentThread().getName() + ")");
                double[] result = new double[array.length - 1];
                for (int i = 0; i < array.length - 1; i++) {
                    result[i] = Math.round((array[i] * array[i + 1]) * 100.0) / 100.0;
                }
                return result;
            }, executorService);

            // Ділення
            CompletableFuture<double[]> divisionTask = CompletableFuture.supplyAsync(() -> {
                System.out.println("Ділення елементів... \t\t(потік " + Thread.currentThread().getName() + ")");
                double[] result = new double[array.length - 1];
                for (int i = 0; i < array.length - 1; i++) {
                    result[i] = array[i + 1] != 0 ? Math.round((array[i] / array[i + 1]) * 100.0) / 100.0 : 0; // Уникаємо ділення на нуль
                }
                return result;
            }, executorService);

            // Очікуємо завершення всіх завдань і виводимо результати
            return CompletableFuture.allOf(additionTask, subtractionTask, multiplicationTask, divisionTask)
                .thenRunAsync(() -> {
                    try {
                        System.out.println("\nРезультати: \t\t\t(потік " + Thread.currentThread().getName() + ")");
                        System.out.println("Додавання: " + Arrays.toString(additionTask.get()));
                        System.out.println("Віднімання: " + Arrays.toString(subtractionTask.get()));
                        System.out.println("Множення: " + Arrays.toString(multiplicationTask.get()));
                        System.out.println("Ділення: " + Arrays.toString(divisionTask.get()));
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }, executorService);
        }));

        calculationsTask.join();
        executorService.shutdown();
        
        System.out.println("\nПрограма завершена. \t\t(потік " + Thread.currentThread().getName() + ")");
    }
}