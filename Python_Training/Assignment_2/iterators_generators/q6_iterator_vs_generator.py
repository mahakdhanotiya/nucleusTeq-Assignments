"""
Question 6 - Iterators and Generators

Difference Between Iterator and Generator

Iterator:
- An iterator is an object that allows
  elements to be accessed one at a time.
- It is created using iter() and accessed
  using next().
- When all elements are processed,
  StopIteration is raised.

Generator:
- A generator is a special function that
  uses the yield keyword.
- It produces values one at a time and
  preserves its state between calls.
- Generators are memory efficient because
  they do not store all values at once.
"""


def demonstrate_iterator() -> None:
    """
    Demonstrate an iterator example.
    """

    numbers = [10, 20, 30]

    # iter() creates an iterator
    # from the given collection.
    iterator = iter(numbers)

    print("Iterator Output:")

    print(next(iterator))
    print(next(iterator))
    print(next(iterator))


def demonstrate_generator():
    """
    Demonstrate a generator example.
    """

    # yield returns one value at a time
    # and preserves the function state.
    yield 10
    yield 20
    yield 30


if __name__ == "__main__":

    print("\n--- Iterator vs Generator ---\n")

    demonstrate_iterator()

    print("\nGenerator Output:")

    for value in demonstrate_generator():
        print(value)

    