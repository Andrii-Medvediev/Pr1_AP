package pr3.task2;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class WorkStealing extends RecursiveTask<Integer> {
    private final List<Path> files;

    // Конструктор приймає список файлів для обробки
    public WorkStealing(List<Path> files) {
        this.files = files;
    }

    @Override
    protected Integer compute() {
        // Якщо завдання достатньо мале, обробляємо його
        if (files.size() <= Main.THRESHOLD) {
            return files.stream()
                    .mapToInt(file -> {
                        try {
                            int charCount = FileUtils.countCharacters(file);
                            System.out.printf("| %-16s | %-14d |\n", file.getFileName().toString(), charCount);
                            return charCount;
                        } catch (Exception e) {
                            e.printStackTrace();
                            return 0;
                        }
                    })
                    .sum();
        } else {
            // Розбиваємо завдання на дві підзадачі
            int mid = files.size() / 2;

            WorkStealing leftTask = new WorkStealing(files.subList(0, mid));
            WorkStealing rightTask = new WorkStealing(files.subList(mid, files.size()));

            invokeAll(leftTask, rightTask);

            return leftTask.join() + rightTask.join();
        }
    }
}
