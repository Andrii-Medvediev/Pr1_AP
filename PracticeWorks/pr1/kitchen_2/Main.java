<<<<<<< HEAD
package Hometasks.pr1.kitchen_2;
=======
package PracticeWorks.pr1.kitchen_2;
>>>>>>> 116c2b79955f6f6dd2be9fe2f1f8c5c662407fbe

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Semaphore;

public class Main {

    static final Semaphore cooks = new Semaphore(4);
    private static boolean isOpen = true;
    static List<Thread> list = Collections.synchronizedList(new ArrayList<Thread>());

    public static synchronized boolean isKitchenOpen() {
        return isOpen;
    }

    public static synchronized void closeKitchen() {
        isOpen = false;
        System.err.println("===================== Кухня закрилася =====================");
    }

    public static void main(String[] args) throws InterruptedException {

        System.err.println("==================== Кухня  відкрилася ====================");

        Runnable kitchen = () -> {
            int i = 0;
            while (isKitchenOpen()) {
                Thread thread = new Thread(new Cook(), "Замовлення " + ++i);
                thread.start();

                list.add(thread);

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

        for(Thread thread: list) {
            thread.join();
        }
        kitchenThread.join();

        System.err.println("==================== Всі кухарі вільні ====================");
    }
}