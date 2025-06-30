import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ProcessFile {
    public static void processFile(String path) throws IOException, LineTooLongException {
        int totalLines = 0;
        int maxLength = 0;
        int minLength = Integer.MAX_VALUE;

        try (FileReader fileReader = new FileReader(path);
             BufferedReader reader = new BufferedReader(fileReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                totalLines++;
                int length = line.length();

                if (length > 1024) {
                    throw new LineTooLongException("Обнаружена строка № " + totalLines + " длиной " + length +
                            " символов, что превышает максимально допустимую длину 1024 символа. Работа приложения будет завершена.");
                }

                if (length > maxLength) {
                    maxLength = length;
                }
                if (length < minLength) {
                    minLength = length;
                }
            }

            System.out.println("Общее количество строк в файле: " + totalLines);
            if (totalLines > 0) {
                System.out.println("Длина самой длинной строки: " + maxLength);
                System.out.println("Длина самой короткой строки: " + minLength);
            } else {
                System.out.println("Файл пуст");
            }
        }
    }
}
class LineTooLongException extends RuntimeException {
    public LineTooLongException(String message) {
        super(message);
    }
}