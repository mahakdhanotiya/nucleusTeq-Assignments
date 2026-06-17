"""
Question 4 - Iterators and Generators

Write a generator to produce
Fibonacci numbers.
"""


def generate_fibonacci(
    limit: int
):
    """
    Generate Fibonacci numbers
    up to the specified count.
    """

    first_number = 0
    second_number = 1

    for _ in range(limit):

        # yield returns one value
        # at a time and preserves
        # the generator state.
        yield first_number

        # Update both Fibonacci numbers
        # for the next iteration.
        first_number, second_number = (
            second_number,
            first_number + second_number
        )


if __name__ == "__main__":

    print("\n--- Fibonacci Generator ---")

    limit = int(input("Enter count: "))

    if limit < 1:
        print("Please enter a positive number.")
    else:
        for number in generate_fibonacci(limit):
            print(number)