"""
Question 8 - Regular Expressions

Create a password validation
program using regex
(minimum length, one digit,
one special character).
"""

import re


def is_valid_password(password: str) -> bool:
    """
    Return True if the password
    satisfies all requirements.
    """

    # (?=.*\d)        -> At least one digit
    # (?=.*[@$!%*?&]) -> At least one special character
    # .{8,16}         -> Length between 8 and 16 characters
    pattern = (
        r"^(?=.*\d)"
        r"(?=.*[@$!%*?&])"
        r".{8,16}$"
    )

    return bool(re.match(pattern, password))


if __name__ == "__main__":

    print("\n--- Password Validation ---")

    password = input("Enter password: ")
    
     # Display password validation result.
    if is_valid_password(password):
        print("Valid password.")
    else:
        print(
            "Password must be 8-16 characters long "
            "and contain at least one digit and "
            "one special character."
        )