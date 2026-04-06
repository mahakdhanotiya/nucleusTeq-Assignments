public class Pattern {
    public static void main(String[] args) {

        // Triangle
        System.out.println("Triangle:");
        for(int i = 1; i <= 5; i++){
            for(int j = 1; j <= i; j++){
                System.out.print("* ");
            }
            System.out.println();
        }

        // Square
        System.out.println("\nSquare:");
        for(int i = 1; i <= 5; i++){
            for(int j = 1; j <= 5; j++){
                System.out.print("* ");
            }
            System.out.println();
        }

        // Inverted Triangle 
        System.out.println("\nInverted Triangle:");
        for(int i = 5; i >= 1; i--){
            for(int j = 1; j <= i; j++){
                System.out.print("* ");
            }
            System.out.println();
        }

        //  Pyramid
    System.out.println("\nPyramid:");
    for(int i = 1; i <= 5; i++){

        for(int j = 1; j <= 5 - i; j++){
           System.out.print(" ");
        }
        for(int j = 1; j <= (2*i - 1); j++){
           System.out.print("*");
        }

        System.out.println();
    }

    }
}