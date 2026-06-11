"""
Variables and Data Types

Questions:
4. Create variables of type int, float, string, and boolean.
   Print their types using type().

5. Write a program to swap two numbers.

6. Take two numbers and print sum, difference,
   multiplication, and division.
"""


# Question 4
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
    
    
# Question 5
def swap_two_numbers(
    first_number: float,
    second_number: float
) -> tuple[float, float]:
    """
    Swap two numbers.
    """
    # A temporary variable prevents either value
    # from being overwritten during swapping
    temporary_value = first_number
    first_number = second_number
    second_number = temporary_value

    return (
        first_number,
        second_number
    )
    

# Question 6
def perform_arithmetic_operations(
    first_number: float,
    second_number: float
) -> None:
    """
    Perform arithmetic operations on two numbers.
    """

    print(f"Sum: {first_number + second_number}")
    
    print(f"Difference: {first_number - second_number}")
    
    print(f"Multiplication: {first_number * second_number}")

    # Handle division by zero edge case
    if second_number == 0:
        print("Division by zero is not allowed.")
    else:
        print(f"Division: {first_number / second_number}")
        
    
if __name__ == "__main__":
    
    display_variable_types()
    
    print("\n--- Swap Two Numbers ---")

    first_number = float(
        input("Enter first number: ")
    )

    second_number = float(
        input("Enter second number: ")
    )

    print(
        f"Before Swapping -> "
        f"First Number: {first_number}, "
        f"Second Number: {second_number}"
    )

    first_number, second_number = swap_two_numbers(
        first_number,
        second_number
    )

    print(
        f"After Swapping -> "
        f"First Number: {first_number}, "
        f"Second Number: {second_number}"
    )
    
    print("\n--- Arithmetic Operations ---")

    first_number = float(
        input("Enter first number: ")
    )

    second_number = float(
        input("Enter second number: ")
    )

    perform_arithmetic_operations(
        first_number,
        second_number
    )
