class Task1 extends Thread {
    public void run() {
        for (int i = 1; i <= 3; i++) {
            System.out.println("Task 1 running: " + i);
        }
    }
}

class Task2 extends Thread {
    public void run() {
        for (int i = 1; i <= 3; i++) {
            System.out.println("Task 2 running: " + i);
        }
    }
}

public class MultiThreadExample {
    public static void main(String[] args) {

        Task1 t1 = new Task1();
        Task2 t2 = new Task2();

        t1.start();
        t2.start();

        System.out.println("Main thread running...");
    }
}
