import Exceptions.InvalidLogFormatException;
import Exceptions.LineTooLongException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

//C:\Users\lfridlikh\Desktop\test_automatiozation\HW\access.log

public class ProcessFile {
    private static int googleBotCount = 0;
    private static int yandexBotCount = 0;
    private static int totalLines = 0;

    public static void processFile(String path) throws IOException, LineTooLongException, InvalidLogFormatException {
        try (FileReader fileReader = new FileReader(path); BufferedReader reader = new BufferedReader(fileReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                totalLines++;
                int length = line.length();
                try {
                    String botName;
                    if (line.matches("(?i).*(YandexBot|Googlebot).*")) {
                    /* Проверяет "yandexbot", "YANDEXBOT", "GoogleBot" и т.д.
                    (?i) — игнорирует регистр.
                    .* — любое количество символов до и после. */

                        // Находим начало блока "(compatible; "
                        int start = line.indexOf("(compatible; ");
                        if (start == -1) {
                            throw new InvalidLogFormatException("В строке: " + totalLines + " бот как бы есть, но его как бы нет");
                        }

                        // Вычисляем начало названия бота
                        int botNameStart = start + "(compatible; ".length();

                        // Находим конец названия бота (первый '/' или ';' после начала)
                        int botNameEnd = line.length();
                        boolean delimiterFound = false;
                        for (int i = botNameStart; i < line.length(); i++) {
                            char c = line.charAt(i);
                            if (c == '/' || c == ';') {
                                botNameEnd = i;
                                delimiterFound = true;
                                break;
                            }
                        }
                        if (!delimiterFound) {
                            throw new InvalidLogFormatException("Не найден разделитель '/' или ';' в строке: " + totalLines);
                        }

                        // Извлекаем название бота и считаем количество
                        botName = line.substring(botNameStart, botNameEnd).trim();
                        if ("Googlebot".equalsIgnoreCase(botName)) {
                            googleBotCount++;
                        } else if ("YandexBot".equalsIgnoreCase(botName)) {
                            yandexBotCount++;
                        }
                    }
                } catch (InvalidLogFormatException e) {
                    System.err.println("Ошибка формата: " + e.getMessage());
                    continue; // Продолжаем обработку следующих строк
                }
                if (length > 1024) {
                    throw new LineTooLongException("Обнаружена строка № " + totalLines + " длиной " + length + " символов, что превышает максимально допустимую длину 1024 символа. Работа приложения будет завершена.");
                }
            }

            if (totalLines > 0) {
                System.out.println(yandexBotCount);
                System.out.println("Общее количество строк в файле: " + totalLines);
                System.out.println("Доля запросов от Googlebot к веб-сайту относительно общего числа сделанных запросов: " + String.format("%.2f", (double) googleBotCount / totalLines * 100) + "%");
                System.out.println("Доля запросов от YandexBot к веб-сайту относительно общего числа сделанных запросов: " + String.format("%.2f", (double) yandexBotCount / totalLines * 100) + "%");
                                     /* "%.2f" — это форматная строка:
                                     % — начало спецификатора формата.
                                    .2 — округление до 2 знаков после запятой.
                                     f — тип данных: число с плавающей точкой (float/double).*/

            } else {
                System.out.println("Файл пуст");
            }
        }
    }
}

