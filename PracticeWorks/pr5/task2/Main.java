package pr5.task2;

import java.util.concurrent.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Програма стартувала... \t\t\t\t(потік " + Thread.currentThread().getName() + ")\n");

        // Створюємо ExecutorService з 4 потоками
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        // Можливі типи відгуків
        String[] reviewTypes = {"Добре", "Середньо", "Погано"};

        // Симуляція отримання відгуків із трьох платформ
        CompletableFuture<List<String>> platform1Reviews = CompletableFuture.supplyAsync(() -> {
            System.out.println("Отримання відгуків із платформи 1... \t\t(потік " + Thread.currentThread().getName() + ")");
            List<String> reviews = generateRandomReviews(reviewTypes, 5);
            sleep(1000);
            System.out.println("Платформа 1 відгуки: " + reviews);
            return reviews;
        }, executorService);

        CompletableFuture<List<String>> platform2Reviews = CompletableFuture.supplyAsync(() -> {
            System.out.println("Отримання відгуків із платформи 2... \t\t(потік " + Thread.currentThread().getName() + ")");
            List<String> reviews = generateRandomReviews(reviewTypes, 6);
            sleep(1500);
            System.out.println("Платформа 2 відгуки: " + reviews);
            return reviews;
        }, executorService);

        CompletableFuture<List<String>> platform3Reviews = CompletableFuture.supplyAsync(() -> {
            System.out.println("Отримання відгуків із платформи 3... \t\t(потік " + Thread.currentThread().getName() + ")");
            List<String> reviews = generateRandomReviews(reviewTypes, 4);
            sleep(500);
            System.out.println("Платформа 3 відгуки: " + reviews);
            return reviews;
        }, executorService);

        // Об'єднуємо всі відгуки
        CompletableFuture<List<String>> allReviews = platform1Reviews
                .thenCombine(platform2Reviews, (reviews1, reviews2) -> {
                    List<String> combined = new ArrayList<>(reviews1);
                    combined.addAll(reviews2);
                    return combined;
                })
                .thenCombine(platform3Reviews, (combined, reviews3) -> {
                    combined.addAll(reviews3);
                    return combined;
                });

        // Перший варіант виводу (детальний аналіз)
        CompletableFuture<String> detailedOutput = allReviews.thenApplyAsync(reviews -> {
            sleep(800); // Затримка для імітації
            Map<String, Integer> sentimentMap = analyzeReviews(reviews);
            StringBuilder result = new StringBuilder();
            sentimentMap.forEach((key, value) -> result.append(key).append(": ").append(value).append("\n"));
            return result.toString();
        }, executorService);

        // Другий варіант виводу (найпопулярніший відгук)
        CompletableFuture<String> popularOutput = allReviews.thenApplyAsync(reviews -> {
            sleep(1000); // Затримка для імітації
            Map<String, Integer> sentimentMap = analyzeReviews(reviews);
            String mostPopular = findMostPopularReview(sentimentMap);
            return "Більше всього відгуків: " + mostPopular + "\n";
        }, executorService);

        // Використовуємо anyOf для отримання швидшого результату
        CompletableFuture<Object> fasterOutput = CompletableFuture.anyOf(detailedOutput, popularOutput);

        // Виводимо результат
        fasterOutput.thenAccept(result -> {
            System.out.println("\nРезультат: \t\t\t\t\t(потік " + Thread.currentThread().getName() + ")");
            System.out.println(result);
        }).join();

        detailedOutput.join();
        popularOutput.join();
        executorService.shutdown();

        System.out.println("Програма завершена. \t\t\t\t(потік " + Thread.currentThread().getName() + ")");
    }

    // Метод для генерації випадкових відгуків
    private static List<String> generateRandomReviews(String[] reviewTypes, int count) {
        Random random = new Random();
        List<String> reviews = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            reviews.add(reviewTypes[random.nextInt(reviewTypes.length)]);
        }
        return reviews;
    }

    // Метод для аналізу відгуків
    private static Map<String, Integer> analyzeReviews(List<String> reviews) {
        Map<String, Integer> sentimentMap = new HashMap<>();
        for (String review : reviews) {
            sentimentMap.put(review, sentimentMap.getOrDefault(review, 0) + 1);
        }
        return sentimentMap;
    }

    // Метод для пошуку найбільш популярного відгуку
    private static String findMostPopularReview(Map<String, Integer> sentimentMap) {
        // Знаходимо максимальне значення серед оцінок
        int maxCount = sentimentMap.values().stream()
                .max(Integer::compareTo)
                .orElse(0);
    
        // Збираємо всі відгуки, які мають максимальну кількість оцінок
        List<String> mostPopularReviews = sentimentMap.entrySet().stream()
                .filter(entry -> entry.getValue() == maxCount)
                .map(Map.Entry::getKey)
                .toList();
    
        // Формуємо рядок для виводу
        if (mostPopularReviews.isEmpty()) {
            return "Немає даних";
        } else if (mostPopularReviews.size() == 1) {
            return mostPopularReviews.get(0);
        } else {
            return String.join(", ", mostPopularReviews); 
        }
    }

    // Метод для симуляції затримки
    private static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}