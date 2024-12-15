package pr4.task1;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
            System.out.println("Виконується логування перед створенням масиву... \t(потік " + Thread.currentThread().getName() + ")");
            try {
                Thread.sleep(100); // Симуляція затримки логування
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            printExecutionTime(taskStart, "Логування");
        }, executorService);

        // Асинхронно створюємо початковий масив символів після логування
        CompletableFuture<Character[]> createArrayTask = logTask.thenCompose(_ -> CompletableFuture.supplyAsync(() -> {
            long taskStart = System.currentTimeMillis();
            System.out.println("Створення масиву символів... \t\t\t\t(потік " + Thread.currentThread().getName() + ")");
            Random random = new Random();
            Character[] array = new Character[20];
            for (int i = 0; i < array.length; i++) {
                array[i] = random.nextBoolean() ? (char) (random.nextInt(26) + 'a') :
                           random.nextBoolean() ? (char) (random.nextInt(10) + '0') :
                           random.nextBoolean() ? ' ' : '\t';
            }
            System.out.println("Масив створено: " + Arrays.toString(array));
            printExecutionTime(taskStart, "Створення масиву");
            return array;
        }, executorService));

        // Фільтруємо алфавітні символи
        CompletableFuture<Character[]> lettersTask = createArrayTask.thenApplyAsync(array -> {
            long taskStart = System.currentTimeMillis();
            System.out.println("Фільтрація алфавітних символів... \t\t\t(потік " + Thread.currentThread().getName() + ")");
            Character[] letters = Arrays.stream(array)
                    .filter(Character::isLetter)
                    .toArray(Character[]::new);
            System.out.println("Алфавітні символи: " + Arrays.toString(letters));
            printExecutionTime(taskStart, "Фільтрація алфавітних символів");
            return letters;
        }, executorService);

        // Фільтруємо пробіли
        CompletableFuture<Character[]> spacesTask = createArrayTask.thenApplyAsync(array -> {
            long taskStart = System.currentTimeMillis();
            System.out.println("Фільтрація пробілів... \t\t\t\t\t(потік " + Thread.currentThread().getName() + ")");
            Character[] spaces = Arrays.stream(array)
                    .filter(Character::isSpaceChar)
                    .toArray(Character[]::new);
            System.out.println("Пробіли: " + Arrays.toString(spaces));
            printExecutionTime(taskStart, "Фільтрація пробілів");
            return spaces;
        }, executorService);

        // Фільтруємо табуляції
        CompletableFuture<Character[]> tabsTask = createArrayTask.thenApplyAsync(array -> {
            long taskStart = System.currentTimeMillis();
            System.out.println("Фільтрація табуляцій... \t\t\t\t(потік " + Thread.currentThread().getName() + ")");
            Character[] tabs = Arrays.stream(array)
                    .filter(ch -> ch == '\t')
                    .toArray(Character[]::new);
            System.out.println("Табуляції: " + java.util.Arrays.toString(tabs));
            printExecutionTime(taskStart, "Фільтрація табуляцій");
            return tabs;
        }, executorService);

        // Фільтруємо інші символи
        CompletableFuture<Character[]> othersTask = createArrayTask.thenApplyAsync(array -> {
            long taskStart = System.currentTimeMillis();
            System.out.println("Фільтрація інших символів... \t\t\t\t(потік " + Thread.currentThread().getName() + ")");
            Character[] others = java.util.Arrays.stream(array)
                    .filter(ch -> !Character.isLetter(ch) && !Character.isSpaceChar(ch) && ch != '\t')
                    .toArray(Character[]::new);
            System.out.println("Інші символи: " + java.util.Arrays.toString(others));
            printExecutionTime(taskStart, "Фільтрація інших символів");
            return others;
        }, executorService);

        // Використання thenRunAsync для виводу фінального повідомлення
        CompletableFuture<Void> finalTask = CompletableFuture.allOf(lettersTask, spacesTask, tabsTask, othersTask)
                .thenRunAsync(() -> {
                    long endTime = System.currentTimeMillis();
                    System.out.println("\nУсі задачі завершено. Загальний час: " + (endTime - startTime) + " мс \t\t(потік " + Thread.currentThread().getName() + ")");
                }, executorService);

        // Очікуємо завершення всіх задач
        finalTask.join();

        System.out.println("Програма завершена. \t\t\t\t\t(потік " + Thread.currentThread().getName() + ")");

        // Завершуємо ExecutorService
        executorService.shutdown();
    }

    // Метод для виводу часу виконання задачі
    private static void printExecutionTime(long start, String taskName) {
        long end = System.currentTimeMillis();
        System.out.println(taskName + " виконано за: " + (end - start) + " мс");
    }
}