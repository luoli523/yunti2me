#!/usr/bin/env python

# Simple Encryption #1 - Alternating Split
#
# For building the encrypted string:
# Take every 2nd char from the string, then the other chars, that are not every 2nd char, and concat them as new String.
# Do this n times!
#
# Examples:
#
# "This is a test!", 1 -> "hsi  etTi sats!"
# "This is a test!", 2 -> "hsi  etTi sats!" -> "s eT ashi tist!"
# Write two methods:
#
# def encrypt(text, n)
# def decrypt(encrypted_text, n)

# For both methods:
# If the input-string is null or empty return exactly this value!
# If n is <= 0 then return the input text.

import unittest


def decrypt(encrypted_text, n):
    if n <= 0:
        return encrypted_text

    def sub_decrypt(text):
        if text in ("", None):
            return text

        even_str = encrypted_text[:int(len(encrypted_text)/2)]
        odd_str = encrypted_text[int(len(encrypted_text)/2):]
        re = ""
        for i in range(len(even_str)):
            re += odd_str[i]
            re += even_str[i]

        if len(encrypted_text) % 2 != 0:
            re += odd_str[-1]

        return re

    for i in range(n):
        encrypted_text = sub_decrypt(encrypted_text)

    return encrypted_text


def decrypt2(encrypted_text, n):
    if encrypted_text in ("", None):
        return encrypted_text

    # mid = len(encrypted_text) // 2  # in python3
    mid = int(len(encrypted_text) / 2)

    for i in range(n):
        first_half = encrypted_text[:mid]
        second_half = encrypted_text[mid:]
        encrypted_text = "".join([second_half[i:i+1] + first_half[i:i+1] for i in range(mid + 1)])

    return encrypted_text



# shability
def encrypt(text, n):
    if n <= 0:
        return text

    def sub_encrypt(text):
        if text in ("", None):
            return text

        odd_str = []
        even_str = []
        [odd_str.append(c) if i % 2 == 0 else even_str.append(c) for i, c in enumerate(text)]
        return ''.join(even_str) + ''.join(odd_str)

    for i in range(n):
        text = sub_encrypt(text)
    return text

# niubility
def encrypt2(text, n):
    for i in range(n):
        text = text[1::2] + text[::2]
    return text

class TestSimpleEncryption1(unittest.TestCase):

    def test_encrypt(self):
        self.assertEqual(encrypt("This is a test!", 0), "This is a test!")
        self.assertEqual(encrypt("This is a test!", 1), "hsi  etTi sats!")
        self.assertEqual(encrypt("This is a test!", 2), "s eT ashi tist!")
        self.assertEqual(encrypt("This is a test!", 3), " Tah itse sits!")
        self.assertEqual(encrypt("This is a test!", 4), "This is a test!")
        self.assertEqual(encrypt("This is a test!", -1), "This is a test!")
        self.assertEqual(encrypt("This kata is very interesting!", 1), "hskt svr neetn!Ti aai eyitrsig")

    def test_encrypt2(self):
        self.assertEqual(encrypt2("This is a test!", 0), "This is a test!")
        self.assertEqual(encrypt2("This is a test!", 1), "hsi  etTi sats!")
        self.assertEqual(encrypt2("This is a test!", 2), "s eT ashi tist!")
        self.assertEqual(encrypt2("This is a test!", 3), " Tah itse sits!")
        self.assertEqual(encrypt2("This is a test!", 4), "This is a test!")
        self.assertEqual(encrypt2("This is a test!", -1), "This is a test!")
        self.assertEqual(encrypt2("This kata is very interesting!", 1), "hskt svr neetn!Ti aai eyitrsig")

    def test_decrypt(self):
        self.assertEqual(decrypt("This is a test!", 0), "This is a test!")
        self.assertEqual(decrypt("hsi  etTi sats!", 1), "This is a test!")
        self.assertEqual(decrypt("s eT ashi tist!", 2), "This is a test!")
        self.assertEqual(decrypt(" Tah itse sits!", 3), "This is a test!")
        self.assertEqual(decrypt("This is a test!", 4), "This is a test!")
        self.assertEqual(decrypt("This is a test!", -1), "This is a test!")
        self.assertEqual(decrypt("hskt svr neetn!Ti aai eyitrsig", 1), "This kata is very interesting!")

    def test_decrypt2(self):
        self.assertEqual(decrypt2("This is a test!", 0), "This is a test!")
        self.assertEqual(decrypt2("hsi  etTi sats!", 1), "This is a test!")
        self.assertEqual(decrypt2("s eT ashi tist!", 2), "This is a test!")
        self.assertEqual(decrypt2(" Tah itse sits!", 3), "This is a test!")
        self.assertEqual(decrypt2("This is a test!", 4), "This is a test!")
        self.assertEqual(decrypt2("This is a test!", -1), "This is a test!")
        self.assertEqual(decrypt2("hskt svr neetn!Ti aai eyitrsig", 1), "This kata is very interesting!")

    def test_empty(self):
        self.assertEqual(encrypt("", 0), "")
        self.assertEqual(decrypt("", 0), "")
        self.assertEqual(encrypt(None, 0), None)
        self.assertEqual(decrypt(None, 0), None)

    def test_empty2(self):
        self.assertEqual(encrypt2("", 0), "")
        self.assertEqual(decrypt2("", 0), "")
        self.assertEqual(encrypt2(None, 0), None)
        self.assertEqual(decrypt2(None, 0), None)

if __name__ == '__main__':
    unittest.main()
