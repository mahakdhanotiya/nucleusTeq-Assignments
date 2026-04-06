import java.util.Scanner;

public class Temperature {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // Celsius to Fahrenheit
        System.out.println("Enter temperature in Celsius:");
        double celsius = sc.nextDouble();

        double fahrenheit = (celsius * 9.0/5) + 32;
        System.out.printf("Converted to Fahrenheit: %.2f °F\n", fahrenheit);

        // Fahrenheit to Celsius
        System.out.println("Enter temperature in Fahrenheit:");
        double tempF = sc.nextDouble();

        double tempC = (tempF - 32) * 5.0/9;
        System.out.printf("Converted to Celsius: %.2f °C\n", tempC);
        
        sc.close();
    }
}
