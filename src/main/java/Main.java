import java.util.Scanner;

public class Main { public static void main(String[] args) { System.out.println("Введите первое число");
    int firstNumber = new Scanner(System.in).nextInt();
    System.out.println("Введите второе число");
    int secondNumber = new Scanner(System.in).nextInt();
    double add = (double) firstNumber + secondNumber;
    double sub = (double) firstNumber - secondNumber;
    double mul = (double) firstNumber * secondNumber;
    double div = (double) firstNumber / secondNumber;
    System.out.println("Сумма: " + (int)add);
    System.out.println("Разность: " + (int)sub);
    System.out.println("Произведение: " + (int)mul);
    if ((firstNumber % secondNumber) !=0) {
        System.out.println("Частное: " + div);}
    else {
        System.out.println("Частное: " + (int)div);}

}
}