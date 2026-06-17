"""
Question 2 - Packaging

Explain the difference
between a module and
a package with an example.
"""

# Module:
# A module is a single Python (.py) file that
# contains functions, classes, variables, or
# reusable code.
#
# Example:
# number_utils.py
#
# def get_square(number):
#     return number * number
#
# Package:
# A package is a collection of related modules
# organized inside a directory containing an
# __init__.py file.
#
# Example:
#
# packaging/
# ├── __init__.py
# ├── number_utils.py
# └── q1_module_usage.py
#
# Difference:
#
# 1. A module is a single Python file, whereas
#    a package is a directory containing multiple
#    modules.
#
# 2. Modules organize reusable code, while
#    packages organize related modules.
#
# 3. A package contains an __init__.py file,
#    whereas a module is simply a .py file.
#
# Example:
# number_utils.py is a module, while packaging/
# is a package because it contains multiple
# modules and an __init__.py file.
#
# Conclusion:
# A module is a single Python file, while a
# package is a collection of related modules.