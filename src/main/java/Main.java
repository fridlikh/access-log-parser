import java.io.File;
import java.util.Scanner;
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
                ProcessFile.processFile(path);
            }
            catch (LineTooLongException ex) {
                System.out.println(ex.getMessage());
                break;
            }
            catch (Exception ex) {
                ex.printStackTrace();
                break;
            }
        }
    }
}