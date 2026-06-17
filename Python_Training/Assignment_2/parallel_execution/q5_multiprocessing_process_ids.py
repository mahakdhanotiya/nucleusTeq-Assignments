"""
Question 5 - Parallel Execution

Write a program to create
two processes that print
their Process IDs.

Process:
A process is an independent
execution unit with its own
memory space and resources.
Each process is identified
by a unique Process ID (PID).
"""

import multiprocessing
import os


def display_process_id() -> None:
    """
    Display the current
    process ID.
    """

    # Get the unique ID of
    # the current process.
    print(
        f"Process ID: {os.getpid()}"
    )


if __name__ == "__main__":

    # Create two processes.
    first_process = multiprocessing.Process(
        target=display_process_id
    )

    second_process = multiprocessing.Process(
        target=display_process_id
    )

    # Start process execution.
    first_process.start()
    second_process.start()

    # Wait for both processes
    # to complete execution.
    first_process.join()
    second_process.join()