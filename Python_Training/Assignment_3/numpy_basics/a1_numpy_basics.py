"""
Assignment 1 - NumPy Basics

Tasks:
1. Create a NumPy array.
2. Calculate mean, max, min, and sum.
3. Perform addition and multiplication
   on two arrays.
4. Create a 3x3 matrix using NumPy.
"""

import numpy as np

ARRAY_VALUES = [10, 20, 30, 40, 50]
FIRST_ARRAY = [1, 2, 3]
SECOND_ARRAY = [4, 5, 6]
MATRIX_SIZE = 3


def display_statistics(numbers: np.ndarray) -> None:
    """
    Display basic statistical
    information for a NumPy array.
    """

    # NumPy provides optimized
    # functions for numerical analysis.
    print("Mean:", np.mean(numbers))
    print("Maximum:", np.max(numbers))
    print("Minimum:", np.min(numbers))
    print("Sum:", np.sum(numbers))


if __name__ == "__main__":
    
    # Question 1
    numbers = np.array(ARRAY_VALUES)

    print("NumPy Array:")
    print(numbers)
    
    # Question 2
    print("\nArray Statistics:")
    display_statistics(numbers)
    
    # Question 3
    first_array = np.array(FIRST_ARRAY)
    second_array = np.array(SECOND_ARRAY)

    print("\nArray Addition:")
    print(first_array + second_array)

    print("\nArray Multiplication:")
    print(first_array * second_array)

    # Question 4
    # Reshape converts a one-dimensional
    # sequence into a matrix structure.
    matrix = np.arange(
        1,
        MATRIX_SIZE * MATRIX_SIZE + 1
    ).reshape(
        MATRIX_SIZE,
        MATRIX_SIZE
    )

    print(f"\n{MATRIX_SIZE}x{MATRIX_SIZE} Matrix:")
    print(matrix)