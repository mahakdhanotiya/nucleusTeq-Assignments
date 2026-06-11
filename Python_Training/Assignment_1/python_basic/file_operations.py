"""
File Handling

Questions:
35. Create a file and write your name into it.

36. Read a file and count words, lines and characters.

37. Append data to existing file.

38. Copy content from one file to another.

39. Search a word in a file.
"""


# Question 35
def write_name_to_file(
    file_name: str,
    name: str
) -> None:
    """
    Create a file and write a name into it.
    """
    with open(file_name, "w") as file:
        file.write(name)


# Question 36
def count_file_contents(
    file_name: str
) -> tuple[int, int, int]:
    """
    Count lines, words,
    and characters in a file.
    """
    with open(file_name, "r") as file:
        content = file.read()
        
    # Calculate lines, words, and characters
    line_count = len(content.splitlines())
    word_count = len(content.split())
    character_count = len(content)

    return (
        line_count,
        word_count,
        character_count
    )


# Question 37
def append_to_file(
    file_name: str,
    content: str
) -> None:
    """
    Append content to an existing file.
    """
    with open(file_name, "a") as file:
        file.write(content)


# Question 38
def copy_file_content(
    source_file: str,
    destination_file: str
) -> None:
    """
    Copy content from one file
    to another.
    """
    with open(source_file, "r") as source:
        content = source.read()

    with open(destination_file, "w") as destination:
        destination.write(content)


# Question 39
def search_word_in_file(
    file_name: str,
    word: str
) -> bool:
    """
    Search for a word in a file.
    """
    with open(file_name, "r") as file:
        content = file.read()
    
    # Check whether the word exists in the file
    return word in content


if __name__ == "__main__":

    print("\n--- Write Name To File ---")

    write_name_to_file(
        "sample.txt",
        "Hello, my name is Mahak."
    )

    print("Name written successfully.")

    print("\n--- Append To File ---")

    append_to_file(
        "sample.txt",
        "\nI am learning Python programming."
    )

    print("Content appended successfully.")

    print("\n--- Count File Contents ---")

    line_count, word_count, character_count = (
        count_file_contents("sample.txt")
    )

    print(f"Lines: {line_count}")
    print(f"Words: {word_count}")
    print(f"Characters: {character_count}")

    print("\n--- Copy File Content ---")

    copy_file_content(
        "sample.txt",
        "copy_sample.txt"
    )

    print("File copied successfully.")

    print("\n--- Search Word In File ---")

    word_found = search_word_in_file(
        "sample.txt",
        "Python"
    )

    print(f"Word Found: {word_found}")