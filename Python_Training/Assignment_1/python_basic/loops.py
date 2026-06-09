"""
Loops

Questions:
12. Print numbers from 1 to 100 using loop.

13. Print multiplication table of a number.

14. Find factorial of a number.

15. Reverse a number using loop.

16. Check whether a number is prime.
"""


def print_numbers_from_one_to_hundred() -> None:
    """
    Print numbers from 1 to 100.
    """
    print("\n--- Numbers from 1 to 100 ---")

    for number in range(1, 101):
        print(number)
        
        
def print_multiplication_table() -> None:
    """
    Print multiplication table of a number.
    """
    print("\n--- Multiplication Table ---")

    number = int(input("Enter a number: "))

    for multiplier in range(1, 11):
        print(f"{number} x {multiplier} = {number * multiplier}")
        
        
def calculate_factorial() -> None:
    """
    Calculate factorial of a number.
    """
    print("\n--- Factorial Calculator ---")

    number = int(input("Enter a number: "))

    if number < 0:
        print("Factorial is not defined for negative numbers.")
        return

    factorial = 1

    for current_number in range(1, number + 1):
        factorial *= current_number

    print(f"Factorial of {number} is {factorial}.")
    
    
def reverse_number() -> None:
    """
    Reverse a number using loop.
    """
    print("\n--- Reverse a Number ---")

    number = int(input("Enter a number: "))
    
    if number < 0:
        print("Please enter a non-negative number.")
        return

    reversed_number = 0

    while number > 0:
        digit = number % 10
        reversed_number = (reversed_number * 10) + digit
        number //= 10

    print(f"Reversed Number: {reversed_number}")
    
    
def check_prime_number() -> None:
    """
    Check whether a number is prime.
    """
    print("\n--- Prime Number Checker ---")

    number = int(input("Enter a number: "))

    if number <= 1:
        print(f"{number} is not a prime number.")
        return

    for divisor in range(2, number):

        # Check if number is divisible by any value other than 1 and itself
        if number % divisor == 0:
            print(f"{number} is not a prime number.")
            return

    print(f"{number} is a prime number.")
    
    
if __name__ == "__main__":
    print_numbers_from_one_to_hundred()
    print_multiplication_table()
    calculate_factorial()
    reverse_number()
    check_prime_number()