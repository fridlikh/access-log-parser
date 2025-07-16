import Exceptions.InvalidLogFormatException;
import Exceptions.LineTooLongException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LogFileParser {
    // Максимальная длина строки (для защиты от переполнения)
    private static final int MAX_LINE_LENGTH = 10_000;

    public static List<LogEntry> processFile(String path)
            throws IOException, LineTooLongException, InvalidLogFormatException {

        List<LogEntry> entries = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Проверка на слишком длинные строки
                if (line.length() > MAX_LINE_LENGTH) {
                    throw new LineTooLongException("Log line exceeds maximum length: " + line.length());
                }

                try {
                    // Создаем новый LogEntry и добавляем в список
                    LogEntry entry = new LogEntry(line);
                    entries.add(entry);
                } catch (IllegalArgumentException e) {
                    throw new InvalidLogFormatException("Invalid log format in line: " + line, e);
                }
            }
        }
        return entries;
    }
}