"""
Question 8 - Exception Handling

Handle FileNotFoundError when trying
to open a file.
"""

from pathlib import Path


def read_file(
    file_name: str
) -> str:
    """
    Open and read a file.
    """

    try:
        # Locate the file relative to
        # the current script location.
        file_path = Path(__file__).parent / file_name

        with open(
            file_path,
            "r",
            encoding="utf-8"
        ) as file:

            return file.read()

    except FileNotFoundError:
        return "File not found."


if __name__ == "__main__":

    print("\n--- File Reader ---")

    file_name = input(
        "Enter file name: "
    )

    print(
        read_file(file_name)
    )