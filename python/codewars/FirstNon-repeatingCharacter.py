#!/usr/bin/env python

# Write a function named first_non_repeating_letter that takes a string input,
# and returns the first character that is not repeated anywhere in the string.
#
# For example, if given the input 'stress', the function should return 't',
# since the letter t only occurs once in the string, and occurs first in the string.
#
# As an added challenge, upper- and lowercase letters are considered the same character,
# but the function should return the correct case for the initial letter.
#
# For example, the input 'sTreSS' should return 'T'.
#
# If a string contains all repeating characters, it should return an empty string ("") or None -- see sample tests.

import unittest


def first_non_repeating_letter(string):
    #your code here


class TestNonRepeatingLetter(unittest.TestCase):

    def test_first_non_repeating_letter_simple(self):
        self.assertEqual(first_non_repeating_letter('a'), 'a')
        self.assertEqual(first_non_repeating_letter('stress'), 't')
        self.assertEqual(first_non_repeating_letter('moonmen'), 'e')

    def test_first_non_repeating_letter_empty(self):
        self.assertEqual(first_non_repeating_letter(''), '')

    def test_first_non_repeating_letter_odd(self):
        self.assertEqual(first_non_repeating_letter('~><#~><'), '#')
        self.assertEqual(first_non_repeating_letter('hello world, eh?'), 'w')

    def test_first_non_repeating_letter_case(self):
        self.assertEqual(first_non_repeating_letter('sTreSS'), 'T')
        self.assertEqual(first_non_repeating_letter('Go hang a salami, I\'m a lasagna hog!'), ',')


if __name__ == '__main__':
    unittest.main()
