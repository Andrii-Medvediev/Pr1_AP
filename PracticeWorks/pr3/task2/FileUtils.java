package pr3.task2;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    // Рекурсивно знаходить усі текстові файли в директорії
    public static List<Path> findTextFiles(Path directory) {
        List<Path> textFiles = new ArrayList<>();
        try {
            Files.walk(directory)
                    .filter(path -> path.toString().endsWith(".txt"))
                    .forEach(textFiles::add);
        } catch (IOException e) {
            System.err.println("Помилка під час пошуку текстових файлів: " + e.getMessage());
        }
        return textFiles;
    }

    // Рахує кількість символів у текстовому файлі
    public static int countCharacters(Path file) {
        try {
            String content = Files.readString(file);
            return content.length();
        } catch (IOException e) {
            System.err.println("Помилка під час читання файлу " + file + ": " + e.getMessage());
            return 0;
        }
    }
}