"""
Question 6 - Parallel Execution

Write a multiprocessing
program to calculate the
square of numbers using
Process class.

Multiprocessing:
It allows multiple processes
to run independently and
perform tasks concurrently.
"""

import multiprocessing


def calculate_square(number: int) -> None:
    """
    Calculate and display
    the square of a number.
    """

    print(
        f"Square of {number}: "
        f"{number ** 2}"
    )


if __name__ == "__main__":
    
    numbers = [1, 2, 3, 4, 5]

    # Ensure the list is not empty.
    if not numbers:
        print("No numbers available.")
    else:

        processes = []

        for number in numbers:

            # Create a process for
            # each calculation.
            process = multiprocessing.Process(
                target=calculate_square,
                args=(number,)
            )

            processes.append(process)

            # Start process execution.
            process.start()

        # Wait for all processes
        # to complete execution.
        for process in processes:
            process.join()