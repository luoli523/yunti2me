#!/usr/bin/env python

# Given an array (arr) as an argument complete the function countSmileys that should return the total number of smiling faces.
#
# Rules for a smiling face:
# - Each smiley face must contain a valid pair of eyes. Eyes can be marked as : or ;
# - A smiley face can have a nose but it does not have to. Valid characters for a nose are - or ~
# - Every smiling face must have a smiling mouth that should be marked with either ) or D.
# No additional characters are allowed except for those mentioned.
# Valid smiley face examples:
# :) :D ;-D :~)
# Invalid smiley faces:
# ;( :> :} :]
#
# Example cases:
#
# countSmileys([':)', ';(', ';}', ':-D']);       // should return 2;
# countSmileys([';D', ':-(', ':-)', ';~)']);     // should return 3;
# countSmileys([';]', ':[', ';*', ':$', ';-D']); // should return 1;
#
# Note: In case of an empty array return 0. You will not be tested with invalid input (input will always be an array).
# Order of the face (eyes, nose, mouth) elements will always be the same

import unittest
from re import findall


def count_smileys(arr):

    def is_smile(item):
        if item[0] != ':' and item[0] != ';':
            return False
        elif len(item) < 2 or len(item) > 3:
            return False
        elif item[len(item) - 1] != ')' and item[len(item) - 1] != 'D':
            return False
        elif len(item) == 3 and item[1] != '-' and item[1] != '~':
            return False
        else:
            return True

    return len(filter(is_smile, arr))


def count_smileys2(arr):
    return len(list(findall(r"[:;][-~]?[)D]", " ".join(arr))))


class TestCountSmile(unittest.TestCase):

    def test_count_smileys(self):
        self.assertEqual(count_smileys([':)', ';(', ';}', ':-D']), 2)
        self.assertEqual(count_smileys([';D', ':-(', ':-)', ';~)']), 3)
        self.assertEqual(count_smileys([';]', ':[', ';*', ':$', ';-D']), 1)
        self.assertEqual(count_smileys([]), 0)
        self.assertEqual(count_smileys([':D', ':~)', ';~D', ':)']), 4)
        self.assertEqual(count_smileys([':)', ':(', ':D', ':O', ':;']), 2)
        self.assertEqual(count_smileys([';]', ':[', ';*', ':$', ';-D']), 1)

    def test_count_smileys2(self):
        self.assertEqual(count_smileys2([':)', ';(', ';}', ':-D']), 2)
        self.assertEqual(count_smileys2([';D', ':-(', ':-)', ';~)']), 3)
        self.assertEqual(count_smileys2([';]', ':[', ';*', ':$', ';-D']), 1)
        self.assertEqual(count_smileys2([]), 0)
        self.assertEqual(count_smileys2([':D', ':~)', ';~D', ':)']), 4)
        self.assertEqual(count_smileys2([':)', ':(', ':D', ':O', ':;']), 2)
        self.assertEqual(count_smileys2([';]', ':[', ';*', ':$', ';-D']), 1)


if __name__ == '__main__':
    unittest.main()
