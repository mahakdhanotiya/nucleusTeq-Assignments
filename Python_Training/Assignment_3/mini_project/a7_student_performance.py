"""
Assignment 7 - Student Performance Analysis

Tasks:
1. Load student data into Pandas.
2. Add Performance column.
3. Create Line Chart and Scatter Plot.
4. Create Seaborn Barplot.
"""

from pathlib import Path

import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns

PASS_MARKS = 65
DATA_FILE = Path(__file__).parent / "students.csv"


def analyze_student_performance() -> None:
    """
    Analyze student performance
    and create visualizations.
    """

    # Load student dataset into Pandas.
    student_dataframe = pd.read_csv(DATA_FILE)

    # Categorize students based on marks.
    student_dataframe["Performance"] = student_dataframe["Marks"].apply(
        lambda marks: "Pass" if marks > PASS_MARKS else "Fail"
    )

    print("Student Data:")
    print(student_dataframe)

    # Line chart showing study hours vs marks.
    plt.figure(figsize=(6, 4))
    plt.plot(
        student_dataframe["Hours_Studied"],
        student_dataframe["Marks"],
        marker="o",
        linewidth=2
    )
    plt.title("Hours Studied vs Marks")
    plt.xlabel("Hours Studied")
    plt.ylabel("Marks")
    plt.grid(True, axis="y")
    plt.tight_layout()
    plt.show()

    # Scatter plot showing relationship between variables.
    plt.figure(figsize=(6, 4))
    plt.scatter(
        student_dataframe["Hours_Studied"],
        student_dataframe["Marks"]
    )
    plt.title("Study Hours vs Marks")
    plt.xlabel("Hours Studied")
    plt.ylabel("Marks")
    plt.tight_layout()
    plt.show()

    # Barplot comparing marks by performance category.
    plt.figure(figsize=(6, 4))
    sns.barplot(
        data=student_dataframe,
        x="Performance",
        y="Marks"
    )
    plt.title("Performance vs Marks")
    plt.ylabel("Marks")
    plt.grid(True, axis="y")
    plt.tight_layout()
    plt.show()


if __name__ == "__main__":
    analyze_student_performance()