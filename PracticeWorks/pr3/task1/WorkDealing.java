package pr3.task1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.List;
import java.util.ArrayList;

public class WorkDealing {
    public static int findMinUsingExecutor(int[][] array, int rows, int firstElement, int threads) {
        ExecutorService executor = Executors.newFixedThreadPool(threads); 
        List<Future<Integer>> futures = new ArrayList<>(); 

        // Розподіл рядків масиву між потоками
        int chunkSize = rows / threads;

        // Цикл для подання завдань до пулу потоків
        for (int i = 0; i < threads; i++) {
            final int startRow = i * chunkSize;
            final int endRow = (i == threads - 1) ? rows : startRow + chunkSize;
            System.out.println("(" + startRow + " " + endRow + ")");

            futures.add(executor.submit(() -> ArrayUtils.findMinElement(array, startRow, endRow, firstElement)));
        }

        int min = Integer.MAX_VALUE;
        try {
            // Цикл для обробки результатів з потоків
            for (Future<Integer> future : futures) {
                min = Math.min(min, future.get());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        executor.shutdown();

        return min;
    }
}
