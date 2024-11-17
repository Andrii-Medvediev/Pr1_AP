package pr1.kitchen;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    static final Semaphore cooks = new Semaphore(4);
    private static boolean isOpen = true;
    private static final AtomicInteger ordersInProgress = new AtomicInteger(0);

    public static synchronized boolean isKitchenOpen() {
        return isOpen;
    }

    public static synchronized void closeKitchen() {
        isOpen = false;
        System.err.println("===================== Кухня закрилася =====================");
    }

    public static synchronized void newOrder() {
        ordersInProgress.incrementAndGet();
    }

    public static synchronized void completeOrder() {
        ordersInProgress.decrementAndGet();
    }

    public static synchronized boolean areOrdersPresent() {
        return ordersInProgress.get() > 0;
    }

    public static void main(String[] args) throws InterruptedException {

        System.err.println("==================== Кухня  відкрилася ====================");

        Runnable kitchen = () -> {
            int i = 0;
            while (isKitchenOpen()) {
                new Thread(new Cook(), "Замовлення " + ++i).start();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread kitchenThread = new Thread(kitchen, "Кухня");
        kitchenThread.start();

        Thread.sleep(10_000);
        closeKitchen();

        while (areOrdersPresent()) {
            Thread.sleep(1000);
        }

        System.err.println("==================== Всі кухарі вільні ====================");
    }
}