"""
Question 5 - Iterators and Generators

Write a generator expression to
generate even numbers from 1 to 50.
"""


def generate_even_numbers():
    """
    Generate even numbers from
    1 to 50 using a generator expression.
    """

    # Generator expression creates
    # values on demand instead of
    # storing them all in memory.
    return (
        number
        for number in range(1, 51)
        if number % 2 == 0
    )


if __name__ == "__main__":

    print("\n--- Even Number Generator ---")

    even_numbers = generate_even_numbers()

    for number in even_numbers:
        print(number)