"""
Assignment 2 - Pandas DataFrame Creation

Tasks:
1. Create an employee DataFrame.
2. Display the first 2 rows.
3. Display summary statistics.
4. Display only IT employees.
5. Add a Bonus column equal
   to 10% of salary.
"""

import pandas as pd

BONUS_PERCENTAGE = 0.10


def display_employee_insights(
    employee_dataframe: pd.DataFrame
) -> None:
    """
    Display the required employee
    DataFrame insights.
    """

    # Question 2
    print("\nFirst Two Rows:")
    print(employee_dataframe.head(2))

    # Question 3
    print("\nSummary Statistics:")
    print(employee_dataframe.describe())

    # Question 4
    print("\nIT Employees:")

    # Filtering helps retrieve
    # records matching a condition.
    it_employees = employee_dataframe[
        employee_dataframe["Department"] == "IT"
    ]

    print(it_employees)


if __name__ == "__main__":

    # Question 1
    employee_data = {
        "Name": ["Rahul", "Priya", "Amit", "Anuj"],
        "Age": [25, 30, 28, 35],
        "Department": ["HR", "IT", "Finance", "IT"],
        "Salary": [30000, 50000, 45000, 60000]
    }

    employee_dataframe = pd.DataFrame(
        employee_data
    )

    print("Employee DataFrame:")
    print(employee_dataframe)

    display_employee_insights(
        employee_dataframe
    )

    # Question 5

    # Bonus is calculated as
    # a percentage of salary.
    employee_dataframe["Bonus"] = (
        employee_dataframe["Salary"]
        * BONUS_PERCENTAGE
    )

    print("\nDataFrame After Adding Bonus:")
    print(employee_dataframe)