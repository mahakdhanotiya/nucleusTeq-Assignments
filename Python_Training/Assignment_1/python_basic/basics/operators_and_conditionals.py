"""
Operators and Conditionals

Questions:
7. Check whether a number is even or odd.

8. Check whether a number is positive, negative, or zero.

9. Find the largest of three numbers.

10. Calculate grade based on marks.

11. Check whether a year is a leap year.
"""

# Question 7
def check_even_or_odd(
    number: int
) -> str:
    """
    Check whether a number is even or odd.
    """
    if number % 2 == 0:
        return "even"

    return "odd"
       

# Question 8        
def check_positive_negative_zero(
    number: float
) -> str:
    """
    Check whether a number is positive, negative, or zero.
    """
    if number > 0:
        return "positive"

    if number < 0:
        return "negative"

    return "zero"
        
  
# Question 9      
def find_largest_number(
    first_number: float,
    second_number: float,
    third_number: float
) -> float:
    """
    Find the largest of three numbers.
    """

    # Comparing all values ensures the function
    # returns the largest number regardless of order
    if (
        first_number >= second_number
        and first_number >= third_number
    ):
        largest_number = first_number
        
    elif (
        second_number >= first_number
        and second_number >= third_number
    ):
        largest_number = second_number
        
    else:
        largest_number = third_number

    return largest_number


# Question 10
def calculate_grade(
    marks: float
) -> str:
    """
    Calculate grade based on marks.
    """

    # Handle invalid marks edge case
    if marks < 0 or marks > 100:
        return "Invalid marks. Marks should be between 0 and 100."

    if marks >= 90:
        return "Grade: A"

    if marks >= 75:
        return "Grade: B"

    if marks >= 50:
        return "Grade: C"

    return "Grade: Fail"
        
    
# Question 11    
def check_leap_year(
    year: int
) -> bool:
    """
    Check whether a year is a leap year.
    """
    # Leap year rules prevent century years
    # from being treated as leap years unless divisible by 400
    if (
        year % 400 == 0
        or (
            year % 4 == 0
            and year % 100 != 0
        )
    ):
        return True
    
    return False
        
        
if __name__ == "__main__":
    
    print("\n--- Even or Odd ---")
    
    number = int(input("Enter a number: "))
    print(
        f"{number} is an "
        f"{check_even_or_odd(number)} number."
    )

    print("\n--- Positive, Negative, or Zero ---")
    
    number = float(input("Enter a number: "))
    print(
        f"{number} is "
        f"{check_positive_negative_zero(number)}."
    )

    print("\n--- Largest of Three Numbers ---")

    first_number = float(input("Enter first number: "))
    second_number = float(input("Enter second number: "))
    third_number = float(input("Enter third number: "))

    print(
        f"The largest number is "
        f"{find_largest_number(first_number, second_number, third_number)}."
    )

    print("\n--- Grade Calculation ---")

    marks = float(input("Enter marks: "))

    print(
        calculate_grade(marks)
    )

    print("\n--- Leap Year Checker ---")

    year = int(input("Enter a year: "))

    if check_leap_year(year):
        print(f"{year} is a leap year.")
    else:
        print(f"{year} is not a leap year.")