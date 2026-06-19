"""
Question 2 - Exception Handling

Write a program to divide two numbers entered by
the user and handle ZeroDivisionError.
"""


def divide_numbers(
    dividend: float,
    divisor: float
) -> float:
    """
    Divide two numbers and return the result.

    Args:
        dividend: Number to be divided.
        divisor: Number by which the dividend
            is divided.

    Returns:
        Division result.

    Raises:
        ZeroDivisionError:
            If divisor is zero.
    """

    return dividend / divisor


def get_division_message(
    dividend: float,
    divisor: float
) -> str:
    """
    Return a success or error message
    based on the division operation.
    """

    try:
        # Separate division logic improves
        # reusability and testability.
        result = divide_numbers(
            dividend,
            divisor
        )

    except ZeroDivisionError:
        return "Division by zero is not allowed."

    return f"Division Result: {result}"


if __name__ == "__main__":

    print("\n--- Division Exception Handling ---")
    
    try:
        dividend = float(
            input("Enter dividend: ")
        )

        divisor = float(
            input("Enter divisor: ")
        )

        print(
            get_division_message(
                dividend,
                divisor
            )
        )

    except ValueError:
        print( "Invalid input. "
            "Please enter numeric values only."
        )