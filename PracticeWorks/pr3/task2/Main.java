package pr3.task2;

import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;

public class Main {
    public static int THRESHOLD;

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            // Вибір папки для обробки
            System.out.println("Виберіть папку для обробки: folder1, folder2, folder3, folder4");
            String folderName = scanner.nextLine();

            Path directory = Path.of("pr3/task2", folderName);

            if (!directory.toFile().exists() || !directory.toFile().isDirectory()) {
                System.err.println("Помилка: Такої директорії не існує.");
                return;
            }

            // Знаходимо всі файли .txt у вибраній директорії
            System.out.println("Сканування файлів у директорії '" + directory + "'.");
            List<Path> textFiles = FileUtils.findTextFiles(directory);
            int folderSize = textFiles.size();

            if (folderSize == 0) {
                System.out.println("У вибраній директорії немає текстових файлів.");
                return;
            } else {
                System.out.println("У директорії '" + directory + "' знаходяться " + folderSize + " файлів.");
            }

            // Розрахунок розміру підзадач
            final int availableProcessors = Runtime.getRuntime().availableProcessors();
            int chunkSize = folderSize / availableProcessors;
            int remainder = folderSize % availableProcessors; 
            THRESHOLD = chunkSize + (remainder > 0 ? 1 : 0);

            // Work Stealing
            System.out.println("\nWorkStealing");
            long start = System.currentTimeMillis();
            try (ForkJoinPool pool = new ForkJoinPool(availableProcessors)) {
                System.out.println("-------------------------------------\n" +
                                   "|       Файл       | К-сть символів |\n" +
                                   "-------------------------------------");
                WorkStealing task = new WorkStealing(textFiles);
                System.out.println("-------------------------------------\n" +
                                   "Загальна кількість символів: " + pool.invoke(task));
            }
            System.out.println("Час виконання: " + (System.currentTimeMillis() - start) + " ms");

            // Work Dealing
            System.out.println("\nWorkDealing");
            start = System.currentTimeMillis();
            System.out.println("-------------------------------------\n" +
                               "|       Файл       | К-сть символів |\n" +
                               "-------------------------------------");
            int totalCharsDealing = WorkDealing.computeTotalCharacters(textFiles, availableProcessors);
            System.out.println("-------------------------------------\n" +
                               "Загальна кількість символів: " + totalCharsDealing);
            System.out.println("Час виконання: " + (System.currentTimeMillis() - start) + " ms");
        }
    }
}