"""
Introduction to Python

Questions:
1. Print welcome message
2. Display Python version
3. Take user input (name and age) and display formatted message
"""

import sys


def print_welcome_message() -> None:
    """
    Display welcome message for Python training.
    """
    print("Welcome to Python Training")
    
    
def display_python_version() -> None:
    """
    Display the installed Python version.
    """
    print(f"Python Version: {sys.version}")
    

def display_user_information() -> None:
    """
    Take user name and age as input
    and display a formatted message.
    """
    user_name = input("Enter your name: ").strip().title()
    user_age = int(input("Enter your age: "))

    if user_age < 0:
        print("Age cannot be negative.")
    else:
        print(f"Hello {user_name}, you are {user_age} years old.")
    
    
if __name__ == "__main__":
    print_welcome_message()
    display_python_version()
    display_user_information()