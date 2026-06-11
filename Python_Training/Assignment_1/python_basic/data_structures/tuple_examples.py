"""
Data Structures - Tuple

Questions:
28. Create a tuple and access elements.

29. Convert tuple into list and modify it.
"""


# Question 28
def display_tuple_elements(
    tuple_data: tuple
) -> None:
    """
    Access and display tuple elements.
    """
    # Access each element using its index
    for index in range(len(tuple_data)):
        print(
            f"Element at index "
            f"{index}: {tuple_data[index]}"
        )
        

# Question 29
def modify_tuple(
    tuple_data: tuple,
    index: int,
    new_value: str | int
) -> list:
    """
    Convert tuple to list and modify an element.
    """
    # Convert tuple to list because tuples are immutable
    modified_list = list(tuple_data)

    # Update the value at the given index
    modified_list[index] = new_value

    return modified_list


if __name__ == "__main__":

    student_data = (
        "Mahak",
        22,
        "Python"
    )

    print("\n--- Tuple Elements ---")

    display_tuple_elements(student_data)

    print("\n--- Tuple to List Conversion ---")

    modified_student_data = modify_tuple(
        student_data,
        1,
        23
    )

    print(
        f"Original Tuple: "
        f"{student_data}"
    )

    print(
        f"Modified List: "
        f"{modified_student_data}"
    )