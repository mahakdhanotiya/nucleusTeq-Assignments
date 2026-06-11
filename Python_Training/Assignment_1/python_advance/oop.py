"""
Object-Oriented Programming (OOP)

Questions:
40. Create a Student class with attributes and display details.

41. Create a Car class with a constructor.

42. Implement inheritance using Person and Employee class.

43. Implement encapsulation using private variables in Bank class.

44. Demonstrate polymorphism using different classes with the same
    method name.
"""


# Question 40
# Represents a student with basic details
class Student:

    def __init__(
        self,
        name: str,
        age: int,
        course: str
    ):
        self.name = name
        self.age = age
        self.course = course

    def display_details(self) -> None:
        print(f"Name: {self.name}")
        print(f"Age: {self.age}")
        print(f"Course: {self.course}")


# Question 41
class Car:

    def __init__(
        self,
        brand: str,
        model: str
    ):
        self.brand = brand
        self.model = model

    def display_car_details(self) -> None:
        print(f"Brand: {self.brand}")
        print(f"Model: {self.model}")


# Question 42
class Person:

    def __init__(
        self,
        name: str
    ):
        self.name = name
# Employee inherits properties from Person
class Employee(Person):

    def __init__(
        self,
        name: str,
        employee_id: int
    ):
        super().__init__(name)
        self.employee_id = employee_id

    def display_employee_details(self) -> None:
        print(f"Name: {self.name}")
        print(f"Employee ID: {self.employee_id}")


# Question 43
class Bank:

    def __init__(
        self,
        balance: float
    ):
        # Double underscore makes balance private
        # and restricts direct access from outside the class
        self.__balance = balance

    def deposit(
        self,
        amount: float
    ) -> None:
        self.__balance += amount

    # Getter method provides controlled access
    # to the private balance variable
    def get_balance(self) -> float:
        return self.__balance


# Question 44
# Both classes use the same method name,
# but each class provides its own implementation
class Dog:

    def make_sound(self) -> None:
        print("Bark")
class Cat:

    def make_sound(self) -> None:
        print("Meow")


if __name__ == "__main__":

    print("\n--- Student Class ---")

    student = Student(
        "Mahak", 22, "Python"
    )
    student.display_details()

    print("\n--- Car Class ---")

    car = Car(
        "Toyota", "Camry"
    )
    car.display_car_details()

    print("\n--- Inheritance ---")

    employee = Employee(
        "Rahul", 101
    )
    employee.display_employee_details()

    print("\n--- Encapsulation ---")

    bank_account = Bank(1000)
    bank_account.deposit(500)

    print(
        f"Balance: "
        f"{bank_account.get_balance()}"
    )

    print("\n--- Polymorphism ---")

    animals = [Dog(), Cat()]

    for animal in animals:
        animal.make_sound()