import java.util.Scanner;

public class ArrayAverage {
    public static void main(String[] args) {

        Scanner sc =new Scanner(System.in);

        System.out.print("Enter number of elements: ");
        int n = sc.nextInt();

        int[] arr = new int[n];
        int sum=0;

        System.out.println("Enter elements:");

        for(int i = 0; i < n; i++){
            arr[i]=sc.nextInt();
            sum = sum + arr[i];

        }

        double average = (double) sum / n;
        System.out.println("Average of array = " + average);

        sc.close();
    
    }
}
