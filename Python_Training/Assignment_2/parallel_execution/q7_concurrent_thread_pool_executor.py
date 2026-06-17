"""
Question 7 - Parallel Execution

Convert a normal function
into parallel execution
using ThreadPoolExecutor.

ThreadPoolExecutor:
It manages a pool of threads
and executes tasks concurrently,
reducing manual thread management.
"""

from concurrent.futures import ThreadPoolExecutor


def calculate_square(number: int) -> int:
    """
    Return the square
    of a number.
    """

    return number ** 2


if __name__ == "__main__":

    numbers = [1, 2, 3, 4, 5]

    # Ensure numbers are available
    # for processing.
    if not numbers:
        print("No numbers available.")
    else:

        # Normal Execution:
        # for number in numbers:
        #     print(calculate_square(number))

        # Parallel Execution:
        # Execute the function using
        # ThreadPoolExecutor.
        with ThreadPoolExecutor() as executor:

            results = executor.map(
                calculate_square,
                numbers
            )

        # Display calculated squares.
        for result in results:
            print(result)