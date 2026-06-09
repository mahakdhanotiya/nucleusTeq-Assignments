"""
Variables and Data Types

Questions:
4. Create variables of type int, float, string, and boolean.
   Print their types using type().

5. Write a program to swap two numbers.

6. Take two numbers and print sum, difference,
   multiplication, and division.
"""


def display_variable_types() -> None:
    """
    Create variables of different data types
    and display their types.
    """
    print("\n--- Variable Types ---")
    integer_value = 10
    float_value = 10.5
    string_value = "Python"
    boolean_value = True

    print(f"Integer Type: {type(integer_value)}")
    print(f"Float Type: {type(float_value)}")
    print(f"String Type: {type(string_value)}")
    print(f"Boolean Type: {type(boolean_value)}")
    
    
def swap_two_numbers() -> None:
    """
    Take two numbers as input
    and swap their values.
    """
    print("\n--- Swap Two Numbers ---")
    first_number = float(input("Enter first number: "))
    second_number = float(input("Enter second number: "))
    print(
        f"Before Swapping -> First Number: {first_number}, "
        f"Second Number: {second_number}"
    )

    first_number, second_number = second_number, first_number

    print(
        f"After Swapping -> First Number: {first_number}, "
        f"Second Number: {second_number}"
    )
    

def perform_arithmetic_operations() -> None:
    """
    Take two numbers as input and
    perform arithmetic operations.
    """
    print("\n--- Arithmetic Operations ---")
    first_number = float(input("Enter first number: "))
    second_number = float(input("Enter second number: "))

    print(f"Sum: {first_number + second_number}")
    print(f"Difference: {first_number - second_number}")
    print(f"Multiplication: {first_number * second_number}")

    if second_number == 0:
        print("Division by zero is not allowed.")
    else:
        print(f"Division: {first_number / second_number}")
        
    
if __name__ == "__main__":
    display_variable_types()
    swap_two_numbers()
    perform_arithmetic_operations()