"""
Data Structures - Set

Questions:
30. Perform union, intersection,and difference on two sets.

31. Remove duplicates from list using set.
"""


# Question 30
def perform_set_operations(
    first_set: set[int],
    second_set: set[int]
) -> tuple[set[int], set[int], set[int]]:
    """
    Perform union, intersection,
    and difference on two sets.
    """
    # Combine all unique elements from both sets
    union_set = first_set.union(second_set)
    
    # Find common elements in both sets
    intersection_set = first_set.intersection(second_set)
    
    # Find elements present only in first_set
    difference_set = first_set.difference(second_set)

    return (
        union_set,
        intersection_set,
        difference_set
    )


# Question 31
def remove_duplicates(
    numbers: list[int]
) -> list[int]:
    """
    Remove duplicates from a list.
    """
    # Convert list to set to remove duplicates
    return list(set(numbers))


if __name__ == "__main__":

    print("\n--- Set Operations ---")

    first_set = {1, 2, 3, 4, 5}
    second_set = {4, 5, 6, 7, 8}

    union_set, intersection_set, difference_set = (
        perform_set_operations(
            first_set,
            second_set
        )
    )

    print(f"First Set: {first_set}")
    print(f"Second Set: {second_set}")
    print(f"Union: {union_set}")
    print(f"Intersection: {intersection_set}")
    print(f"Difference: {difference_set}")

    print("\n--- Remove Duplicates Using Set ---")

    numbers = [10, 20, 10, 30, 20, 40]

    unique_numbers = remove_duplicates(
        numbers
    )

    print(f"Original List: {numbers}")

    print(
        f"List Without Duplicates: "
        f"{unique_numbers}"
    )