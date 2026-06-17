"""
Question 2 - Iterators and Generators

Create a custom iterator class
that returns numbers from 1 to N.
"""


class NumberIterator:
    """
    Custom iterator that returns
    numbers from 1 to N.
    """

    def __init__(self, limit: int) -> None:
        self.limit = limit
        self.current = 1

    def __iter__(self):

        # Return the iterator object
        # so iteration can begin.
        return self

    def __next__(self) -> int:

        # Stop iteration when all
        # numbers have been returned.
        if self.current > self.limit:
            raise StopIteration

        # Store the current value
        # before moving to the next one.
        number = self.current
        self.current += 1

        return number


if __name__ == "__main__":

    print("\n--- Custom Iterator ---")

    limit = int(input("Enter N: "))
    
    if limit < 1:
        print("Please enter a positive number.")
    else:
        for number in NumberIterator(limit):
            print(number)