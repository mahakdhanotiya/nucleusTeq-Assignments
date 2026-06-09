"""
Operators and Conditionals

Questions:
7. Check whether a number is even or odd.

8. Check whether a number is positive,
   negative, or zero.

9. Find the largest of three numbers.

10. Calculate grade based on marks.

11. Check whether a year is a leap year.
"""

def check_even_or_odd() -> None:
    """
    Check whether a number is even or odd.
    """
    print("\n--- Even or Odd ---")

    number = int(input("Enter a number: "))

    if number % 2 == 0:
        print(f"{number} is an even number.")
    else:
        print(f"{number} is an odd number.")
        
        
def check_positive_negative_zero() -> None:
    """
    Check whether a number is positive, negative, or zero.
    """
    print("\n--- Positive, Negative, or Zero ---")

    number = float(input("Enter a number: "))

    if number > 0:
        print(f"{number} is a positive number.")
    elif number < 0:
        print(f"{number} is a negative number.")
    else:
        print("The number is zero.")
        
        
def find_largest_number() -> None:
    """
    Find the largest of three numbers.
    """
    print("\n--- Largest of Three Numbers ---")

    first_number = float(input("Enter first number: "))
    second_number = float(input("Enter second number: "))
    third_number = float(input("Enter third number: "))

    if first_number >= second_number and first_number >= third_number:
        largest_number = first_number
    elif second_number >= first_number and second_number >= third_number:
        largest_number = second_number
    else:
        largest_number = third_number

    print(f"The largest number is {largest_number}.")
    
    
def calculate_grade() -> None:
    """
    Calculate grade based on marks.
    """
    print("\n--- Grade Calculator ---")

    marks = float(input("Enter marks: "))

    if marks < 0 or marks > 100:
        print("Invalid marks. Marks should be between 0 and 100.")
    elif marks >= 90:
        print("Grade: A")
    elif marks >= 75:
        print("Grade: B")
    elif marks >= 50:
        print("Grade: C")
    else:
        print("Grade: Fail")
        
        
def check_leap_year() -> None:
    """
    Check whether a year is a leap year.
    """
    print("\n--- Leap Year Checker ---")

    year = int(input("Enter a year: "))

    if year % 400 == 0 or (year % 4 == 0 and year % 100 != 0):
        print(f"{year} is a leap year.")
    else:
        print(f"{year} is not a leap year.")
        
        
if __name__ == "__main__":
    check_even_or_odd()
    check_positive_negative_zero()
    find_largest_number()
    calculate_grade()
    check_leap_year()