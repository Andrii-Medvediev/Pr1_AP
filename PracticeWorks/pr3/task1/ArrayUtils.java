package pr3.task1;

import java.util.Random;

public class ArrayUtils {
    // Генерація елементів двох вимірного масиву
    public static int[][] initArray(int rows, int cols) {
        Random random = new Random();
        int[][] array = new int[rows][cols];
        int maxInt = Integer.MAX_VALUE;

        // Генеруємо перший елемент масиву у діапазоні [0, maxInt / 2]
        array[0][0] = random.nextInt(maxInt / 2);

        // Генеруємо решту елементів масиву у діапазоні [0, maxInt]
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (i == 0 && j == 0) continue;
                array[i][j] = random.nextInt(maxInt);
            }
        }
        return array;
    }

    // Знаходження мінімального елемента в масиві (для синхронного способу)
    public static int findMinElement(int[][] array, int firstElement) {
        int min = Integer.MAX_VALUE;
        for (int[] row : array) {
            for (int element : row) {
                // Мінімальний елемент має бути в два рази більшим ніж перший елемент
                if (element > firstElement * 2 && element < min) {
                    min = element;
                }
            }
        }
        return min;
    }

    // Знаходження мінімального елемента в масиві (для асинхронного способу)
    public static int findMinElement(int[][] array, int startRow, int endRow, int firstElement) {
        int min = Integer.MAX_VALUE;
        for (int i = startRow; i < endRow; i++) {
            for (int element : array[i]) {
                // Мінімальний елемент має бути в два рази більшим ніж перший елемент
                if (element > firstElement * 2 && element < min) {
                    min = element;
                }
            }
        }
        return min;
    }
}
