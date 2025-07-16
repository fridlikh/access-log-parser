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
                // 1. Читаем файл и получаем список LogEntry
                List<LogEntry> entries = LogFileParser.processFile(path);

                // 2. Обрабатываем каждую запись
                for (LogEntry entry : entries) {
                    // Здесь можно делать что-то с каждой записью
                    System.out.println("Обработанная запись: " + entry);
/*
                    // Или выводить отдельные поля:
                    System.out.printf("IP: %s, Method: %s, URL: %s%n",
                            entry.getIpAddress(),
                            entry.getReqMethod(),
                            entry.getReqURL());

 */
                }

                System.out.println("Всего обработано записей: " + entries.size());

            } catch (LineTooLongException ex) {
                System.out.println(ex.getMessage());
                break;
            } catch (InvalidLogFormatException ex) {
                System.out.println("Ошибка формата лога: " + ex.getMessage());
                // Можно продолжить для следующего файла вместо break
                continue;
            } catch (Exception ex) {
                ex.printStackTrace();
                break;
            }
        }
    }
}