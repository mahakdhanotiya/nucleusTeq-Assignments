"""
Data Structures - List

Questions:
25. Create a list of 10 numbers and find
    sum, max, sort it, and remove duplicates.

26. Count even and odd numbers in a list.

27. Reverse a list without using reverse().
"""


# Question 25
def perform_list_operations(
    numbers: list[int]
) -> tuple[int, int, list[int], list[int]]:
    """
    Find sum, maximum value,
    sorted list, and remove duplicates.
    """
    total = sum(numbers)
    maximum_number = max(numbers)
    sorted_numbers = sorted(numbers)
    unique_numbers = list(set(numbers))

    return (
        total,
        maximum_number,
        sorted_numbers,
        unique_numbers
    )


# Question 26
def count_even_odd_numbers(
    numbers: list[int]
) -> tuple[int, int]:
    """
    Count even and odd numbers in a list.
    """
    even_count = 0
    odd_count = 0

    for number in numbers:
        if number % 2 == 0:
            even_count += 1
        else:
            odd_count += 1

    return even_count, odd_count


# Question 27
def reverse_list(
    numbers: list[int]
) -> list[int]:
    """
    Reverse a list without using reverse().
    """
    return numbers[::-1]


if __name__ == "__main__":

    print("\n--- List Operations ---")

    numbers = [
        10, 5, 20, 15, 10,
        25, 30, 5, 40, 50
    ]

    total, maximum_number, sorted_numbers, unique_numbers = (
        perform_list_operations(numbers)
    )

    print(f"Original List: {numbers}")
    print(f"Sum: {total}")
    print(f"Maximum Number: {maximum_number}")
    print(f"Sorted List: {sorted_numbers}")
    print(
        f"List After Removing Duplicates: "
        f"{unique_numbers}"
    )

    print("\n--- Even and Odd Count ---")

    even_count, odd_count = (
        count_even_odd_numbers(numbers)
    )

    print(f"Even Numbers Count: {even_count}")
    print(f"Odd Numbers Count: {odd_count}")

    print("\n--- Reverse List ---")

    reversed_numbers = reverse_list(numbers)

    print(f"Original List: {numbers}")
    print(f"Reversed List: {reversed_numbers}")