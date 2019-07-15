#!/usr/bin/env python

import unittest


def high_and_low(numbers):
    # ...
    num_list = numbers.split()
    num_list = map(int, num_list)
    max_num = num_list[0]
    min_num = num_list[0]

    for i in num_list:
        if i < min_num:
            min_num = i

        if i > max_num:
            max_num = i

    return str(max_num) + " " + str(min_num)


def high_and_low2(numbers):
    # ...
    num_list = numbers.split()
    max_num = int(num_list[0])
    min_num = int(num_list[0])

    for i in num_list:
        n = int(i)
        if n < min_num:
            min_num = n

        if n > max_num:
            max_num = n

    return str(max_num) + " " + str(min_num)


def high_and_low3(numbers):
    nn = [int(s) for s in numbers.split()]
    return "%d %d" % (max(nn), min(nn))


def high_and_low4(numbers):
    nn = map(int, numbers.split())
    return "%d %d" % (max(nn), min(nn))


class TestHignAndLow(unittest.TestCase):

    def test_high_and_low1(self):
        numbers = "9 12 15 3 4 2 3 -2 8 1 0"
        self.assertEqual(high_and_low(numbers), "15 -2")

    def test_high_and_low2(self):
        numbers = "9 12 15 3 4 2 3 -2 8 1 0"
        self.assertEqual(high_and_low2(numbers), "15 -2")

    def test_high_and_low3(self):
        numbers = "9 12 15 3 4 2 3 -2 8 1 0"
        self.assertEqual(high_and_low3(numbers), "15 -2")

    def test_high_and_low4(self):
        numbers = "9 12 15 3 4 2 3 -2 8 1 0"
        self.assertEqual(high_and_low4(numbers), "15 -2")


def main():
    numbers = "9 12 15 3 4 2 3 -2 8 1 0"
    print(high_and_low(numbers))
    print(high_and_low2(numbers))
    print(high_and_low3(numbers))
    print(high_and_low4(numbers))


if __name__ == '__main__':
    unittest.main()
