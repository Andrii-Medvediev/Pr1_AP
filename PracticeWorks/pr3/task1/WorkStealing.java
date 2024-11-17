package pr3.task1;

import java.util.concurrent.RecursiveTask;

public class WorkStealing extends RecursiveTask<Integer> {
    private final int[][] array;
    private final int startRow;
    private final int endRow;
    private final int firstElement;

    // Конструктор для ініціалізації завдання
    public WorkStealing(int[][] array, int startRow, int endRow, int firstElement) {
        this.array = array;
        this.startRow = startRow;
        this.endRow = endRow;
        this.firstElement = firstElement;
    }

    @Override
    protected Integer compute() {
        if (endRow - startRow <= Main.THRESHOLD) {
            // Пошук мінімального елемента в заданому діапазоні
            System.out.println("(" + startRow + " " + endRow + ")");
            return ArrayUtils.findMinElement(array, startRow, endRow, firstElement);
        } else {
            // Розділення задачі на дві підзадачі
            int midRow = (startRow + endRow) / 2;

            // Створення та виконання підзадач
            WorkStealing leftTask = new WorkStealing(array, startRow, midRow, firstElement);
            WorkStealing rightTask = new WorkStealing(array, midRow, endRow, firstElement);

            invokeAll(leftTask, rightTask);

            // Об'єднання результатів підзадач
            return Math.min(leftTask.join(), rightTask.join());
        }
    }
}