"""
Question 3 - Exception Handling

Write a program using try-except-else-finally
to read a number from a file and print its square.
"""

from pathlib import Path

def calculate_square(number: float) -> float:
    """
    Return the square of a number.
    """

    return number ** 2

def get_square_message(
    file_name: str
) -> str:
    """
    Read a number from a file and
    return its square.
    """

    file = None

    try:
        # Locate the file relative to the
        # current script location.
        file_path = (
            Path(__file__).parent / file_name
        )
        file = open(
            file_path,
            "r",
            encoding="utf-8"
        )

        # Converting file content to float
        # allows both integer and decimal values.
        number = float(
            file.read().strip()
        )

    except FileNotFoundError:
        return "File not found."

    except ValueError:
        return (
            "Invalid data. "
            "File must contain a valid number."
        )

    else:
        # Separate square calculation improves
        # reusability and testability.
        square = calculate_square(number)

        return (
            f"Square of {number} "
            f"is {square}."
        )

    finally:
        # Closing the file in finally ensures
        # the resource is released regardless
        # of success or failure.
        if file is not None:
            file.close()
            
            
if __name__ == "__main__":
    
    print("\n--- File Square Calculator ---")

    file_name = input(
        "Enter file name: "
    )

    print(
        get_square_message(file_name)
    )