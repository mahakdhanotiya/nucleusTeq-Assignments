//Abstract Class
abstract class Animal {
    abstract void sound();
}

// Abstract class implementation
class Dog extends Animal {
    void sound() {
        System.out.println("Dog barks");
    }
}

// Interface
interface Vehicle {
    void start();
}

// Interface implementation
class Car implements Vehicle {
    public void start() {
        System.out.println("Car starts");
    }
}

public class InterfaceAbstract {
    public static void main(String[] args) {

        Dog d = new Dog();
        d.sound();

        Car c = new Car();
        c.start();
    }
}