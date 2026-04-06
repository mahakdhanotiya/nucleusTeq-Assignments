import java.util.Scanner;

public class Operators {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("Enter first number:");
        int a = sc.nextInt();

        System.out.println("Enter second number:");
        int b = sc.nextInt();

        // Arithmetic Operators
        System.out.println("Addition: " + (a + b));
        System.out.println("Subtraction: " + (a - b));
        System.out.println("Multiplication: " + (a * b));
        
        if (b!=0){
        System.out.println("Division: " + (a / b));
         System.out.println("Modulus: " + (a % b));
        }
        else{
            System.out.println("Division not possible (b is 0)");
        }

        // Relational Operators
        System.out.println("a > b: " + (a > b));
        System.out.println("a < b: " + (a < b));
        System.out.println("a == b: " + (a == b));
        System.out.println("a != b: " + (a != b));
        System.out.println("a >= b: " + (a >= b));
        System.out.println("a <= b: " + (a <= b));

        // Logical Operators
        System.out.println("Logical AND: " + (a > 0 && b > 0));
        System.out.println("Logical OR: " + (a > 0 || b > 0));
        System.out.println("Logical NOT: " + !(a > b));

    }
}