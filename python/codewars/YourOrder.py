#!/usr/bin/env python

# Your task is to sort a given string. Each word in the string will contain a single number.
# This number is the position the word should have in the result.
#
# Note: Numbers can be from 1 to 9. So 1 will be the first word (not 0).
#
# If the input string is empty, return an empty string.
# The words in the input String will only contain valid consecutive numbers.
#
# Examples
# "is2 Thi1s T4est 3a"  -->  "Thi1s is2 3a T4est"
# "4of Fo1r pe6ople g3ood th5e the2"  -->  "Fo1r the2 g3ood 4of th5e pe6ople"
# ""  -->  ""

import unittest


def order(sentence):
    def num_in_str(word):
        base = 1
        re = 0
        for c in word[::-1]:
            if c.isdigit():
                re += int(c) * base
                base *= 10
        return re

    words = sentence.split()
    words.sort(key=num_in_str)
    return " ".join(words)


def order2(sentence):
    return " ".join(sorted(sentence.split(), key=lambda x: int(filter(str.isdigit, x))))


class TestOrderFunction(unittest.TestCase):

    def test_order(self):
        self.assertEqual(order("is2 Thi1s T4est 3a"), "Thi1s is2 3a T4est")
        self.assertEqual(order("4of Fo1r pe6ople g3ood th5e the2"), "Fo1r the2 g3ood 4of th5e pe6ople")
        self.assertEqual(order(""), "")

    def test_order2(self):
        self.assertEqual(order2("is2 Thi1s T4est 3a"), "Thi1s is2 3a T4est")
        self.assertEqual(order2("4of Fo1r pe6ople g3ood th5e the2"), "Fo1r the2 g3ood 4of th5e pe6ople")
        self.assertEqual(order2(""), "")


def main():
    print(order2("is2 Thi1s T4est 3a"), "Thi1s is2 3a T4est")
    print(order2("4of Fo1r pe6ople g3ood th5e the2"), "Fo1r the2 g3ood 4of th5e pe6ople")
    print(order2(""), "")


if __name__ == '__main__':
    unittest.main()

