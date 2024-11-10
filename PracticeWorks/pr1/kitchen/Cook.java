package Hometasks.pr1.kitchen;

import java.util.concurrent.atomic.AtomicInteger;

public class Cook implements Runnable {

    private static final int NUM_COOKS = 4; // Кількість кухарів
    private static final AtomicInteger cookCounter = new AtomicInteger(0); // Лічильник для черговості кухарів
    private int cookId; // Ідентифікатор кухаря

    @Override
    public void run() {
        try {
            String orderName = Thread.currentThread().getName(); // Ідентифікатор замовлення

            System.out.printf("Розміщення %s.\n", orderName);
            Thread.sleep(1000); // Імітація розмішення замовлення
            Main.newOrder();

            // Безкінечний цикл очікування на вільного кухаря
            while (true) {
                // Перевіряємо, чи відкрита кухня. Якщо кухня закрита, завершуємо обробку замовлення
                if (!Main.isKitchenOpen()) {
                    System.out.printf("Кухня закрита. Ресторан не може приготувати %s.\n", orderName);
                    Main.completeOrder();
                    return;
                }

                // Чекаємо на доступного кухаря (семафор гарантує, що одночасно працює не більше 4 кухарів)
                if (Main.cooks.tryAcquire()) {
                    // Призначаємо кухаря по черговості
                    cookId = cookCounter.getAndIncrement() % NUM_COOKS + 1;

                    System.out.printf("Кухар %d прийняв %s та розпочинає приготування.\n", cookId, orderName);
                    Thread.sleep(2000); // Імітація приготування страви

                    System.out.printf("Кухар %d завершив %s.\n", cookId, orderName);
                    Thread.sleep(20);

                    Main.cooks.release();
                    break;
                } else {
                    Thread.sleep(100);  // Затримка перед повторною спробою
                }
            }

            Main.completeOrder();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
