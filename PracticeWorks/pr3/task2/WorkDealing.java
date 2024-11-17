package pr3.task2;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class WorkDealing {
    public static int computeTotalCharacters(List<Path> files, int threads) {
        ExecutorService executor = Executors.newFixedThreadPool(threads);

        try {
            // Тут короткий коментар
            List<Future<Integer>> futures = files.stream()
                .map(file -> executor.submit(() -> {
                    int charCount = FileUtils.countCharacters(file);
                    System.out.printf("| %-16s | %-14d |\n", file.getFileName().toString(), charCount);
                    return charCount;
                }))
                .toList();

            // Тут короткий коментар
            int totalChars = 0;
            for (Future<Integer> future : futures) {
                totalChars += future.get();
            }
            return totalChars;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            executor.shutdown();
        }
    }
}