package pr2;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Main2 {
    // Метод для обробки частини тексту
    public static Callable<Map<String, Integer>> countWordsPart(String[] words) {
        return () -> {
            Map<String, Integer> wordCount = new ConcurrentHashMap<>();
            for (String word : words) {
                word = word.toLowerCase().replaceAll("[.,]", "");
                wordCount.merge(word, 1, Integer::sum);
            }
            return wordCount;
        };
    }

    public static void main(String[] args) throws Exception {

        // Текст для аналізу
        String text = "This is a sample text.\n" + "This text\tis for testing word count.\n" + "Word count\tis done by splitting\n" + "the text into parts.";

        // Розбиваємо текст на окремі слова
        String[] words = text.split("\\s+");

        // Розбиваємо на частини для обробки різними потоками
        List<String[]> parts = Arrays.asList(
            Arrays.copyOfRange(words, 0, words.length / 3),
            Arrays.copyOfRange(words, words.length / 3, 2 * words.length / 3),
            Arrays.copyOfRange(words, 2 * words.length / 3, words.length)
        );

        // Пул потоків
        ExecutorService executor = Executors.newFixedThreadPool(3);

        // Колекція для збереження результатів
        ConcurrentHashMap<String, Integer> finalWordCount = new ConcurrentHashMap<>();

        // Список завдань Callable
        List<Future<Map<String, Integer>>> futures = Arrays.asList(
            executor.submit(countWordsPart(parts.get(0))),
            executor.submit(countWordsPart(parts.get(1))),
            executor.submit(countWordsPart(parts.get(2)))
        );

        // Обробка результатів
        for (Future<Map<String, Integer>> future : futures) {
            // Очікуємо на завершення завдання
            while (!future.isDone()) {
                System.out.println("Завдання ще виконується...");
                Thread.sleep(100); 
            }
            // Додаємо результати з потоку до загальної мапи
            Map<String, Integer> result = future.get();
            result.forEach((word, count) -> finalWordCount.merge(word, count, Integer::sum));
        }

        // Закриваємо пул потоків
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        // Виведення результатів
        System.out.println("Частота слів у тексті:");
        finalWordCount.forEach((word, count) -> System.out.println(word + ": " + count));
    }
}
