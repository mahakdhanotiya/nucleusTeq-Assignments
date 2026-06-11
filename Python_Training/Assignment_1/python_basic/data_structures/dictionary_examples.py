"""
Data Structures - Dictionary

Questions:
32. Create a student dictionary and access values.

33. Count frequency of characters in a string using dictionary.

34. Merge two dictionaries.
"""


# Question 32
def access_student_details(
    student: dict[str, str | int]
) -> None:
    """
    Access dictionary values using keys.
    """
    print(f"Name: {student['name']}")
    print(f"Age: {student['age']}")
    print(f"Course: {student['course']}")


# Question 33
def count_character_frequency(
    text: str
) -> dict[str, int]:
    """
    Count frequency of characters
    in a string.
    """
    # Convert text to lowercase for case-insensitive counting
    text = text.lower()
    
    frequency = {}

    # Count occurrences of each character
    for character in text:
        frequency[character] = (
            frequency.get(character, 0) + 1
        )

    return frequency


# Question 34
def merge_dictionaries(
    first_dictionary: dict,
    second_dictionary: dict
) -> dict:
    """
    Merge two dictionaries.
    """
    
    # Combine key-value pairs from both dictionaries
    return first_dictionary | second_dictionary


if __name__ == "__main__":

    print("\n--- Student Dictionary ---")

    student = {
        "name": "Mahak",
        "age": 22,
        "course": "Python"
    }

    access_student_details(student)

    print("\n--- Character Frequency ---")

    text = "python"
    frequency = count_character_frequency(text)

    print(f"String: {text}")
    print(f"Frequency: {frequency}")

    print("\n--- Merge Dictionaries ---")

    first_dictionary = {"name": "Mahak"}
    second_dictionary = {"course": "Python"}

    merged_dictionary = merge_dictionaries(
        first_dictionary,
        second_dictionary
    )

    print(f"First Dictionary: {first_dictionary}")
    print(f"Second Dictionary: {second_dictionary}")
    print(f"Merged Dictionary: {merged_dictionary}")
    