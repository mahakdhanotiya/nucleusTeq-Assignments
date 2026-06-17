"""
Question 3 - Testing and Debugging

Create a function with a logical bug
and use pdb to identify the issue.
"""

import pdb


def calculate_average(total_marks: int, subject_count: int) -> float:
    """
    Return the average marks.
    """
    
    # Pause execution and inspect
    # variable values step-by-step.
    pdb.set_trace()

    # Bug: average should use /
    return total_marks * subject_count


if __name__ == "__main__":
    print(calculate_average(500, 5))