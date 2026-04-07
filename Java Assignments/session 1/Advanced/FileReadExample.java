import java.io.File;
import java.util.Scanner;

public class FileReadExample {
    public static void main(String[] args) {

        try {
            File file = new File("data.txt");
            Scanner sc = new Scanner(file);

            while (sc.hasNextLine()) {
                System.out.println(sc.nextLine());
            }

            sc.close();

        } catch (Exception e) {
            System.out.println("Error reading file:" +e.getMessage()) ;
        }
    }
}