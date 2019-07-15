#!/usr/bin/env python

# Simple, given a string of words, return the length of the shortest word(s).
# String will never be empty and you do not need to account for different data types.

import unittest


def find_short(s):
    words_len = [len(w) for w in s.split()]
    return min(words_len)


def find_short2(s):
    words_len = map(len, s.split())
    return min(words_len)


def find_short3(s):
    return len(min(s.split(), key=len))


class TestFindShort(unittest.TestCase):

    def test_find_short(self):
        self.assertEqual(find_short("bitcoin take over the world maybe who knows perhaps"), 3)
        self.assertEqual(find_short("turns out random test cases are easier than writing out basic ones"), 3)
        self.assertEqual(find_short("lets talk about javascript the best language"), 3)
        self.assertEqual(find_short("i want to travel the world writing code one day"), 1)
        self.assertEqual(find_short("Lets all go on holiday somewhere very cold"), 2)

        self.assertEqual(find_short2("bitcoin take over the world maybe who knows perhaps"), 3)
        self.assertEqual(find_short2("turns out random test cases are easier than writing out basic ones"), 3)
        self.assertEqual(find_short2("lets talk about javascript the best language"), 3)
        self.assertEqual(find_short2("i want to travel the world writing code one day"), 1)
        self.assertEqual(find_short2("Lets all go on holiday somewhere very cold"), 2)

        self.assertEqual(find_short3("bitcoin take over the world maybe who knows perhaps"), 3)
        self.assertEqual(find_short3("turns out random test cases are easier than writing out basic ones"), 3)
        self.assertEqual(find_short3("lets talk about javascript the best language"), 3)
        self.assertEqual(find_short3("i want to travel the world writing code one day"), 1)
        self.assertEqual(find_short3("Lets all go on holiday somewhere very cold"), 2)


if __name__ == '__main__':
    unittest.main()
