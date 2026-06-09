"""
Modules

Questions:
22. Use math module to find square root,
    power, and factorial.

23. Generate random numbers using
    random module.

24. Create your own module and import it.
"""

import math
import random
from calculator import add_numbers


def demonstrate_math_module() -> None:
    """
    Demonstrate math module functions.
    """
    print("\n--- Math Module Examples ---")

    number = int(
        input("Enter a number: ")
    )
    
    if number < 0:
        print("Please enter a non-negative number.")
        return

    print(
        f"Square Root: "
        f"{math.sqrt(number)}"
    )

    print(
        f"Power (number²): "
        f"{math.pow(number, 2)}"
    )

    print(
        f"Factorial: "
        f"{math.factorial(number)}"
    )
    
    
def generate_random_numbers() -> None:
    """
    Generate random numbers using
    random module.
    """
    print("\n--- Random Numbers ---")

    for _ in range(5):
        print(
            random.randint(1, 100)
        )
        

def demonstrate_custom_module() -> None:
    """
    Demonstrate custom module import.
    """
    print("\n--- Custom Module Example ---")

    first_number = int(
        input("Enter first number: ")
    )

    second_number = int(
        input("Enter second number: ")
    )

    result = add_numbers(
        first_number,
        second_number
    )

    print(f"Result: {result}")
    
    
if __name__ == "__main__":
    demonstrate_math_module()
    generate_random_numbers()
    demonstrate_custom_module()