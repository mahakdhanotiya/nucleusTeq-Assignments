"""
Question 1 - Packaging

Import utility functions
from another module.
"""

from number_utils import (
    get_cube,
    get_square,
)

# Sample value used to
# demonstrate module functions.
TEST_NUMBER = 5


def display_results() -> None:
    """
    Display utility
    function results.
    """
    
    # Call functions imported
    # from another module.
    print(f"Square: {get_square(TEST_NUMBER)}")
    print(f"Cube: {get_cube(TEST_NUMBER)}")


if __name__ == "__main__":
    display_results()