"""
Assignment 3 - Data Cleaning

Tasks:
1. Detect missing values.
2. Replace missing Age with mean.
3. Replace missing Salary with 0.
"""

import pandas as pd

DEFAULT_SALARY = 0

if __name__ == "__main__":

    employee_data = {
        "Name": ["Rahul", "Priya", "Anuj"],
        "Age": [25, None, 29],
        "Salary": [30000, 40000, None]
    }

    employee_dataframe = pd.DataFrame(
        employee_data
    )

    print("Original DataFrame:")
    print(employee_dataframe)
    
    # Question 1
    print("\nMissing Values:")

    # isnull() identifies
    # incomplete records.
    print(
        employee_dataframe.isnull()
    )
    
    # Question 2
    
    # Missing ages are replaced
    # with the average age.
    employee_dataframe["Age"] = (
        employee_dataframe["Age"].fillna(
            employee_dataframe["Age"].mean()
        )
    )
    
    # Question 3

    # Missing salaries are replaced
    # with a default value.
    employee_dataframe["Salary"] = (
        employee_dataframe["Salary"].fillna(
            DEFAULT_SALARY
        )
    )

    print("\nCleaned DataFrame:")
    print(employee_dataframe)