"""
Modules

Questions:
22. Use math module to find square root, power, and factorial.

23. Generate random numbers using random module.

24. Create your own module and import it.
"""

import math
import random
from Python_Training.Assignment_1.python_basic.modules.calculator import add_numbers


# Question 22
def demonstrate_math_module(
    number: int
) -> None:
    """
    Demonstrate math module functions.
    """
    
    # Handle invalid input for square root and factorial
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
    
    
# Question 23    
def generate_random_numbers() -> None:
    """
    Generate random numbers using
    random module.
    """
    print("\n--- Random Numbers ---")

    # Limiting the range keeps the generated
    # values easy to read and verify
    for _ in range(5):
        print(
            random.randint(1, 100)
        )
        

# Question 24
def demonstrate_custom_module(
    first_number: int,
    second_number: int
) -> None:
    """
    Demonstrate custom module import.
    """
    
    # Importing and using a custom module
    # demonstrates code reusability
    result = add_numbers(
        first_number,
        second_number
    )

    print(f"Result: {result}")
    
    
if __name__ == "__main__":
    
    print("\n--- Math Module Examples ---")
    
    number = int(
        input("Enter a number: ")
    )
    demonstrate_math_module(number)
    
    generate_random_numbers()
    
    print("\n--- Custom Module Example ---")

    first_number = int(
        input("Enter first number: ")
    )

    second_number = int(
        input("Enter second number: ")
    )
    demonstrate_custom_module(first_number, second_number)