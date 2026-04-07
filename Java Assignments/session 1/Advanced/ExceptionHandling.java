import java.util.Scanner;

public class ExceptionHandling {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        try {

            System.out.print("Enter number: ");
            int a = sc.nextInt();

            System.out.print("Enter divisor: ");
            int b = sc.nextInt();

            int result = a / b;

            System.out.println(result);

        } catch (ArithmeticException e) {
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println("Program continues...");
    }
}
