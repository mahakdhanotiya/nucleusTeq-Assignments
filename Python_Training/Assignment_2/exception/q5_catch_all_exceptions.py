"""
Question 5 - Exception Handling

Write a program that catches all exceptions
and prints the error message.
"""


def get_list_value(
    values: list[int],
    index: int
) -> int:
    """
    Return the value at the specified index.
    """

    return values[index]


def get_value_message(
    values: list[int],
    index_input: str
) -> str:
    """
    Return a success or error message.
    """

    try:
        # Converting user input to an integer
        # validates the provided index.
        index = int(index_input)

        value = get_list_value(
            values,
            index
        )

    except Exception as error:
        # Catch all exceptions and display
        # the corresponding error message.
        return f"Error: {error}"

    return f"Value at index {index}: {value}"


if __name__ == "__main__":

    print("\n--- Catch All Exceptions ---")

    numbers = [10, 20, 30, 40, 50]

    index = input("Enter an index: ")

    print(
        get_value_message(
            numbers,
            index
        )
    )