package PracticeWorks.pr2;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.*;

public class ArrayProcessing {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Scanner scanner = new Scanner(System.in);
        int lowerBound = 0, upperBound = 1000;

        System.out.println("Чи хочете ви ввести діапазон чисел? (yes/no):");
        String userInput = scanner.nextLine().toLowerCase();
 
        if (userInput.equals("yes")) {
            System.out.print("Введіть нижню межу (>= 0): ");
            lowerBound = scanner.nextInt();
            System.out.print("Введіть верхню межу (<= 1000): ");
            upperBound = scanner.nextInt();

            // Перевірка на коректність введеного діапазону
            if (lowerBound < 0 || upperBound > 1000 || lowerBound >= upperBound) {
                String errorMessage = (lowerBound < 0 ? "Помилка: Нижня межа повинна бути більшою або дорівнювати 0. \n" : "") +
                                      (upperBound > 1000 ? "Помилка: Верхня межа повинна бути меншою або дорівнювати 1000. \n" : "") +
                                      (lowerBound >= upperBound ? "Помилка: Нижня межа повинна бути меншою ніж верхня. \n" : "");
                System.err.println(errorMessage.trim());
                System.exit(0);
            }
        }

        // Генеруємо масиви
        Random random = new Random();
        int numberOfArrays = 4; // Кількість масивів
        CopyOnWriteArraySet<int[]> arrays = new CopyOnWriteArraySet<>();

        // Створення масивів випадкових чисел у заданому діапазоні
        for (int i = 1; i <= numberOfArrays; i++) {
            int arraySize = 40 + random.nextInt(21); // Випадковий розмір масиву від 40 до 60
            int[] array = random.ints(arraySize, lowerBound, upperBound).toArray();
            arrays.add(array);
        }

        // Запит користувача: синхронна чи асинхронна обробка
        System.out.println("Виберіть метод обробки: 1 - Синхронний, 2 - Асинхронний");
        int processingChoice = scanner.nextInt();

        if (processingChoice == 1) {
            processSynchronously(arrays); 
        } else if (processingChoice == 2) {
            processAsynchronously(arrays);
        } else {
            System.err.println("Помилка: Невірний вибір.");
            System.exit(0);
        }
        
        scanner.close();
    }

    // Синхронна обробка з запуском потоків по одному
    private static void processSynchronously(CopyOnWriteArraySet<int[]> arrays) throws InterruptedException {
        ExecutorService executorService = Executors.newSingleThreadExecutor(); // Використовується один потік
        CopyOnWriteArraySet<Double> averages = new CopyOnWriteArraySet<>(); // Середні значення масивів
        long start = System.currentTimeMillis(); // Початок вимірювання часу
        final int[] arrayIndex = {1}; // Лічильник номерів масивів

        // Поетапне оброблення масивів
        for (int[] array : arrays) {
            final int currentIndex = arrayIndex[0]++;
            Future<Double> future = executorService.submit(() -> {
                double average = calculateAverage(array); // Обчислення середнього значення
                System.out.println("Масив " + currentIndex + ": " + Arrays.toString(array));
                System.out.println("Середнє значення масиву " + currentIndex + ": " + average);
                return average;
            });

            try {
                averages.add(future.get(1000, TimeUnit.MILLISECONDS)); // Очікуємо завершення потоку
            } catch (TimeoutException e) {
                System.err.println("Час виконання задачі завершився.");
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Помилка виконання задачі: " + e.getMessage());
            }
        }

        executorService.shutdown();

        // Обчислення загального середнього значення
        double overallAverage = averages.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        long end = System.currentTimeMillis();

        System.out.println("Загальне середнє значення: " + overallAverage);
        System.out.println("Час обробки: " + (end - start) + " мс");
    }
    

    // Асинхронна обробка
    private static void processAsynchronously(CopyOnWriteArraySet<int[]> arrays) throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(arrays.size()); // Пул потоків
        CopyOnWriteArraySet<Future<Double>> futures = new CopyOnWriteArraySet<>();
        CopyOnWriteArraySet<Double> averages = new CopyOnWriteArraySet<>(); // Середні значення масивів
        long start = System.currentTimeMillis(); // Початок вимірювання часу
        final int[] arrayIndex = {1}; // Лічильник масивів
        final int[] taskNumber = {1}; // Лічильник задач

        // Додавання завдань у пул потоків
        for (int[] array : arrays) {
            final int currentIndex = arrayIndex[0]++; 
            Callable<Double> task = () -> {
                double average = calculateAverage(array); // Обчислення середнього значення
                System.out.println("Масив " + currentIndex + ": " + Arrays.toString(array));
                System.out.println("Середнє значення масиву " + currentIndex + ": " + average);
                return average; 
            };
            futures.add(executorService.submit(task));
        }

        // Обробка результатів кожної задачі
        for (Future<Double> future : futures) {
            try {
                averages.add(future.get(1000, TimeUnit.MILLISECONDS)); // Очікуємо результат
            } catch (TimeoutException e) {
                future.cancel(true);
                System.err.println("Час виконання задачі " + taskNumber[0] + " завершився.");
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Помилка виконання задачі " + taskNumber[0] + ": " + e.getMessage());
            }

            // Перевірка статусу завдання
            if (future.isCancelled()) {
                System.out.println("Задача " + taskNumber[0] + " була скасована.");
            } else if (future.isDone()) {
                System.out.println("Задача " + taskNumber[0] + " була виконана успішно.");
            }

            taskNumber[0]++;
        }

        // Обчислення загального середнього значення
        double overallAverage = averages.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        long end = System.currentTimeMillis();
        
        executorService.shutdown();

        System.out.println("Загальне середнє значення: " + overallAverage);
        System.out.println("Час обробки: " + (end - start) + " мс");

    }

    // Розрахунок середнього значення масиву
    private static double calculateAverage(int[] array) {
        return array.length == 0 ? 0 : (double) Arrays.stream(array).sum() / array.length;
    }
}