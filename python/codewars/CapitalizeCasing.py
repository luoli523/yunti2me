#!/usr/bin/env python

import unittest


def toJadenCase(string):
    w = [w.capitalize() for w in string.split()]
    return ' '.join(w)


class TestJadenCase(unittest.TestCase):

    def testJadenCase(self):
        quote = "How can mirrors be real if our eyes aren't real"
        self.assertEqual(toJadenCase(quote), "How Can Mirrors Be Real If Our Eyes Aren't Real")


def main():
    quote = "How can mirrors be real if our eyes aren't real"
    print(toJadenCase(quote))


if __name__ == "__main__":
    unittest.main()
