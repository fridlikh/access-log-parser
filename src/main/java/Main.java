import java.io.File;
import java.util.Scanner;
import java.util.List;
import Exceptions.*;

public class Main {
    public static void main(String[] args) {
        int count = 0;
        while (true) {
            System.out.println("Введите путь к файлу:");
            String path = new Scanner(System.in).nextLine();
            File file = new File(path);
            boolean fileExists = file.exists();
            boolean isDirectory = file.isDirectory();

            if (!fileExists) {
                System.out.println("Указанный файл не существует");
                continue;
            }
            if (isDirectory) {
                System.out.println("Указанный путь является путём к папке, а не к файлу");
                continue;
            } else {
                count++;
                System.out.print("Путь указан верно. ");
                System.out.println("Это файл номер " + count);
            }

            try {
                // Создаем объект для сбора статистики
                Statistics stats = new Statistics();

                // 1. Читаем файл и получаем список LogEntry
                List<LogEntry> entries = LogFileParser.processFile(path);
                int entryNumber = 1;

                // 2. Обрабатываем каждую запись
                for (LogEntry entry : entries) {
                    // Добавляем запись в статистику
                    stats.addEntry(entry);

                    // Здесь можно делать что-то с каждой записью
                    System.out.println("Обработанная строка №: " + entryNumber);

                    //вызов UserAgent
                    String userAgentStr = entry.getUserAgent(); //Получает строку User-Agent из объекта LogEntry entry — это объект класса LogEntry, который уже содержит разобранные данные из строки лога. getUserAgent() — это метод, который возвращает строку User-Agent (если она есть)
                    if (userAgentStr != null) {
                        try {
                            UserAgent userAgent = new UserAgent(userAgentStr); // Создаёт новый объект класса UserAgent
                            System.out.printf("Browser: %s%nOS: %s%n",
                                    userAgent.getBrowser(),
                                    userAgent.getOs());
                        } catch (IllegalArgumentException e) {
                            System.err.println("⚠ Пропущен некорректный UserAgent: " + e.getMessage());
                        }
                    }

                    // Или выводить отдельные поля:
                    System.out.printf("IP: %s%nTimestamp: %s%nMethod: %s%nURL: %s%nRespCode: %s%nBytesSent: %s%nRefer: %s%nUserAgent: %s%n-----------------------------------%n",
                            entry.getIpAddress(),
                            entry.getReqDateAndTime(),
                            entry.getReqMethod(),
                            entry.getReqURL(),
                            entry.getRespCode(),
                            entry.getBytesSent(),
                            entry.getRefer(),
                            entry.getUserAgent());

                    entryNumber++;
                }

                // Выводим статистику после обработки всех записей
                System.out.println("\n====== СТАТИСТИКА ======");
                System.out.printf("Общий трафик: %.2f байт%n", stats.getTotalTraffic());
                System.out.println("Период данных: с " + stats.getMinTime() + " по " + stats.getMaxTime());
                System.out.printf("Средний трафик в час: %.2f байт/час%n", stats.getTrafficRate());
                System.out.println("Всего обработано строк: " + entries.size());

         /*   } catch (LineTooLongException ex) {
                System.out.println(ex.getMessage());
                break;
            } catch (InvalidLogFormatException ex) {
                System.out.println("Ошибка формата лога: " + ex.getMessage());
                // Можно продолжить для следующего файла вместо break
                continue;*/
            } catch (Exception ex) {
                ex.printStackTrace();
                break;
            }
        }
    }
}