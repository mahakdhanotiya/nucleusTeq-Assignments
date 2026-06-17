"""
Question 8 - Parallel Execution

Convert a normal function
into parallel execution
using ProcessPoolExecutor.

ProcessPoolExecutor:
It manages a pool of processes
and executes tasks concurrently,
utilizing multiple CPU cores.
"""

from concurrent.futures import ProcessPoolExecutor


def calculate_cube(number: int) -> int:
    """
    Return the cube
    of a number.
    """

    return number ** 3


if __name__ == "__main__":

    numbers = [1, 2, 3, 4, 5]

    # Ensure numbers are available
    # for processing.
    if not numbers:
        print("No numbers available.")
    else:

        # Normal Execution:
        # for number in numbers:
        #     print(calculate_cube(number))

        # Parallel Execution:
        # Execute the function using
        # ProcessPoolExecutor.
        with ProcessPoolExecutor() as executor:

            results = executor.map(
                calculate_cube,
                numbers
            )

        # Display calculated cubes.
        for result in results:
            print(result)