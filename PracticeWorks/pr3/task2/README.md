# Опис програми

Програма виконує підрахунок кількості символів у текстових файлах .txt, які знаходяться у вибраній користувачем директорії. Користувач вводить назву папки, після чого програма знаходить усі .txt файли, підраховує кількість символів у кожному файлі, а також загальну кількість символів у всіх файлах. Результати виводяться у табличному форматі разом із часом виконання двох способів обробки: Work Stealing (з використанням Fork/Join Framework) і Work Dealing (з використанням ExecutorService).

Клас Main забезпечує основну логіку програми, включаючи взаємодію з користувачем, пошук файлів та виконання обробки обома способами. Клас FileUtils відповідає за пошук .txt файлів у папці та підрахунок кількості символів у кожному з них.

Обробка файлів реалізована двома способами. Work Dealing використовує статичний розподіл задач між потоками, що забезпечує швидкість і ефективність при рівномірному навантаженні. Work Stealing використовує динамічний розподіл задач, що дозволяє краще справлятися із нерівномірним навантаженням за рахунок перерозподілу задач між потоками.

# Опис результатів

У всіх тестах Work Dealing був швидшим завдяки статичному розподілу задач, що зменшує накладні витрати. Work Stealing показав трохи гірший результат через додаткові витрати на динамічне управління задачами.

# Висновки

- Work Dealing краще підходить для задач із рівномірним навантаженням. 
- Work Stealing ефективний для нерівномірного розподілу задач, забезпечуючи стабільність результатів.