"""
Assignment 4 - Data Analysis

Tasks:
1. Find average salary by department.
2. Find maximum salary by department.
3. Count employees per department.
"""

import pandas as pd


if __name__ == "__main__":

    employee_data = {
        "Name": ["Rahul", "Priya", "Amit", "Anuj"],
        "Department": ["HR", "IT", "Finance", "IT"],
        "Salary": [30000, 50000, 45000, 60000]
    }

    employee_dataframe = pd.DataFrame(
        employee_data
    )

    print("Employee DataFrame:")
    print(employee_dataframe)

    # GroupBy allows analysis
    # of data for each department.
    grouped_data = employee_dataframe.groupby(
        "Department"
    )

    # Question 1
    print("\nAverage Salary by Department:")

    # Mean helps understand the
    # typical salary in a department.
    print(
        grouped_data["Salary"].mean()
    )
    
    # Question 2
    print("\nMaximum Salary by Department:")

    # Max identifies the highest
    # salary within each department.
    print(
        grouped_data["Salary"].max()
    )

    # Question 3
    print("\nEmployee Count by Department:")

    # Count shows how many employees
    # belong to each department.
    print(
        grouped_data["Name"].count()
    )