import java.util.Scanner;

public class AreaCalculator{
    public static void main(String[] args) {

        Scanner sc = new Scanner (System.in);

        System.out.println("Enter your choice");
        System.out.println("1. Circle"); 
        System.out.println("2. Rectangle");
         System.out.println("3. Triangle");
        int choice = sc.nextInt();

        if(choice == 1){

           System.out.println("Enter radius:");
            double radius = sc.nextDouble();

            double area = 3.14 * radius * radius;
            System.out.println("Area = " + area);
        }      
        else if(choice == 2){

            System.out.println("Enter length:");
            double length = sc.nextDouble();

            System.out.println("Enter width:");
            double width = sc.nextDouble();

            double area = length * width;
            System.out.println("Area = " + area);

        }       
        else if(choice == 3){
            
            System.out.println("Enter base:");
            double base = sc.nextDouble();

            System.out.println("Enter height:");
            double height = sc.nextDouble();

            double area = 0.5 * base * height;
            System.out.println("Area = " + area);

        }       
        else {
           System.out.println("Invalid choice");
        }
    }
}