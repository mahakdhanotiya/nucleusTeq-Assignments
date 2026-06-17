"""
Question 4 - Packaging

Use the mathematical
operations package.
"""

from operations import (
    add,
    divide,
    multiply,
    subtract,
)

# Sample values used to
# demonstrate operations.
FIRST_NUMBER = 20
SECOND_NUMBER = 5


def display_results() -> None:
    """
    Display operation results.
    """

    # Call functions imported
    # from the package module.
    print(f"Addition: {add(FIRST_NUMBER, SECOND_NUMBER)}")
    print(
        f"Subtraction: "
        f"{subtract(FIRST_NUMBER, SECOND_NUMBER)}"
    )
    print(
        f"Multiplication: "
        f"{multiply(FIRST_NUMBER, SECOND_NUMBER)}"
    )
    print(
        f"Division: "
        f"{divide(FIRST_NUMBER, SECOND_NUMBER)}"
    )


if __name__ == "__main__":
    display_results()