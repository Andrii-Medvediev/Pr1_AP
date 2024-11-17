package pr3.task1;

import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;

public class Main {
    public static int THRESHOLD;

    public static void main(String[] args) {
        int rows, cols;

        // Введеня користувачем кількості рядків та стовпців двовимірного масиву
        try (Scanner scanner = new Scanner(System.in)) {
            try {
                System.out.println("Введіть кількість рядків масиву: ");
                rows = Integer.parseInt(scanner.nextLine());
                System.out.println("Введіть кількість стовпців масиву: ");
                cols = Integer.parseInt(scanner.nextLine());

                if (rows <= 0 || cols <= 0) {
                    throw new IllegalArgumentException("\nПомилка: Розміри масиву повинні бути додатніми цілими числами.");
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("\nПомилка: Розміри масиву повинні бути додатніми цілими числами.");
            }
        }

        System.out.println("Кількість елементів у масиві: " + (rows * cols) + "\n");

        // Розрахунок розміру підзадач
        final int availableProcessors = Runtime.getRuntime().availableProcessors();
        int chunkSize = rows / availableProcessors;
        int remainder = rows % availableProcessors;
        THRESHOLD = chunkSize + (remainder > 0 ? 1 : 0);

        // Ініціалізація масиву
        long start = System.currentTimeMillis();
        int[][] array = ArrayUtils.initArray(rows, cols);
        int firstElement = array[0][0];
        // System.out.println("Двохвимірний масив: " + array);
        System.out.println("Час ініціалізації: " + (System.currentTimeMillis() - start) + " ms");
        System.out.println("Перший елемент: " + firstElement + "\n");

        // Синхронний спосіб виконання розрахунків
        System.out.println("Synchronized");
        start = System.currentTimeMillis();
        System.out.println("Мінімальний елемент: " + ArrayUtils.findMinElement(array, firstElement));
        System.out.println("Час виконання: " + (System.currentTimeMillis() - start) + " ms" + "\n");

        // Асинхронний спосіб виконання розрахунків (WorkStealing)
        System.out.println("WorkStealing");
        start = System.currentTimeMillis();
        try (ForkJoinPool pool = new ForkJoinPool(availableProcessors)) {
            WorkStealing task = new WorkStealing(array, 0, rows, firstElement);
            System.out.println("Мінімальний елемент: " + pool.invoke(task));
        }
        System.out.println("Час виконання: " + (System.currentTimeMillis() - start) + " ms" + "\n");

        // Асинхронний спосіб виконання розрахунків (WorkDealing)
        System.out.println("WorkDealing");
        start = System.currentTimeMillis();
        int minElement = WorkDealing.findMinUsingExecutor(array, rows, firstElement, availableProcessors);
        System.out.println("Мінімальний елемент: " + minElement);
        System.out.println("Час виконання: " + (System.currentTimeMillis() - start) + " ms");
    }
}
