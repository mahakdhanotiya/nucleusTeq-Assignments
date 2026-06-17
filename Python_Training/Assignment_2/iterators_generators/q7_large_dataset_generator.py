"""
Question 7 - Iterators and Generators

Write a program that processes a
large dataset using a generator
instead of storing all values in a list.
"""


def generate_numbers(
    limit: int
):
    """
    Generate numbers one at a time
    without storing them in memory.
    """

    for number in range(1, limit + 1):

        # yield returns values lazily,
        # making generators memory efficient.
        yield number


if __name__ == "__main__":

    print(
        "\n--- Large Dataset Processing ---"
    )

    limit = 1_000_000

    processed_records = 0

    # Process records one at a time
    # without storing the entire dataset.
    for number in generate_numbers(limit):
        processed_records += 1

    print(
        f"Processed {processed_records:,} records."
    )
