#!/usr/bin/env python

import unittest


def binary_array_to_number(arr):
    r = 0
    for i in range(len(arr)):
        if arr[i] > 0:
            r += arr[i] << (len(arr) - i - 1)
    return r


def binary_array_to_number2(arr):
    return int("".join(map(str, arr)), 2)


def binary_array_to_number3(arr):
    return int("".join(str(a) for a in arr), 2)


class TestBinaryArrayToNumber(unittest.TestCase):

    def test_binary_array_to_number1(self):
        self.assertEqual(binary_array_to_number([0,0,0,1]), 1)
        self.assertEqual(binary_array_to_number([0,0,1,0]), 2)
        self.assertEqual(binary_array_to_number([1,1,1,1]), 15)
        self.assertEqual(binary_array_to_number([0,1,1,0]), 6)

    def test_binary_array_to_number2(self):
        self.assertEqual(binary_array_to_number2([0,0,0,1]), 1)
        self.assertEqual(binary_array_to_number2([0,0,1,0]), 2)
        self.assertEqual(binary_array_to_number2([1,1,1,1]), 15)
        self.assertEqual(binary_array_to_number2([0,1,1,0]), 6)

    def test_binary_array_to_number3(self):
        self.assertEqual(binary_array_to_number3([0,0,0,1]), 1)
        self.assertEqual(binary_array_to_number3([0,0,1,0]), 2)
        self.assertEqual(binary_array_to_number3([1,1,1,1]), 15)
        self.assertEqual(binary_array_to_number3([0,1,1,0]), 6)


if __name__ == '__main__':
    unittest.main()
