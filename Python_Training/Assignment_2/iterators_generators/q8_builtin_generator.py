"""
Question 8 - Iterators and Generators

Show an example of a built-in
generator (like range) and
iterate over it.
"""


def display_numbers() -> None:
    """
    Display numbers using range().
    """

    # range() generates values lazily
    # instead of storing them in memory.
    for number in range(1, 6):
        print(number)


if __name__ == "__main__":

    print("\n--- Built-in Generator Example ---")

    display_numbers()