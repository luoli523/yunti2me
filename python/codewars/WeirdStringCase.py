#!/usr/bin/env python

# Write a function toWeirdCase (weirdcase in Ruby) that accepts a string,
# and returns the same string with all even indexed characters in each word upper cased,
# and all odd indexed characters in each word lower cased.
#
# The indexing just explained is zero based, so the zero-ith index is even,
# therefore that character should be upper cased.
#
# The passed in string will only consist of alphabetical characters and spaces(' ').
# Spaces will only be present if there are multiple words. Words will be separated by a single space(' ').
#
# Examples:
# to_weird_case('String'); # => returns 'StRiNg'
# to_weird_case('Weird string case') # => returns 'WeIrD StRiNg CaSe'

import unittest


def to_weird_case(string):
    words = string.split(' ')
    result = []
    for w in words:
        result.append(''.join([w[i].upper() if i % 2 == 0 else w[i].lower() for i in range(len(w))]))

    return ' '.join(result)


# loop a list and trace the index and value in the meanwhile
def to_weird_case2(string):

    def to_weird_case_word(word):
        return ''.join([c.upper() if i % 2 == 0 else c.lower() for i, c in enumerate(word)])

    return ' '.join([to_weird_case_word(w) for w in string.split()])

class TestWeirdCase(unittest.TestCase):

    def test_to_weird_case(self):
        self.assertEqual(to_weird_case('This'), 'ThIs')
        self.assertEqual(to_weird_case('is'), 'Is')
        self.assertEqual(to_weird_case('This is a test'), 'ThIs Is A TeSt')
        self.assertEqual(to_weird_case('Weird string case'), 'WeIrD StRiNg CaSe')

    def test_to_weird_case2(self):
        self.assertEqual(to_weird_case2('This'), 'ThIs')
        self.assertEqual(to_weird_case2('is'), 'Is')
        self.assertEqual(to_weird_case2('This is a test'), 'ThIs Is A TeSt')
        self.assertEqual(to_weird_case2('Weird string case'), 'WeIrD StRiNg CaSe')


def main():
    print(to_weird_case('This'))
    print(to_weird_case('is'))
    print(to_weird_case('This is a test'))
    print(to_weird_case('Weird string case'))


if __name__ == '__main__':
    unittest.main()
