#!/usr/bin/env python

# Simple, given a string of words, return the length of the shortest word(s).
# String will never be empty and you do not need to account for different data types.


def find_short(s):
    words_len = [len(w) for w in s.split()]
    return min(words_len)


def find_short2(s):
    words_len = map(len, s.split())
    return min(words_len)


def main():
    print(find_short("bitcoin take over the world maybe who knows perhaps"))
    print(find_short("turns out random test cases are easier than writing out basic ones"))
    print(find_short("lets talk about javascript the best language"))
    print(find_short("i want to travel the world writing code one day"))
    print(find_short("Lets all go on holiday somewhere very cold"))

    print(find_short2("bitcoin take over the world maybe who knows perhaps"))
    print(find_short2("turns out random test cases are easier than writing out basic ones"))
    print(find_short2("lets talk about javascript the best language"))
    print(find_short2("i want to travel the world writing code one day"))
    print(find_short2("Lets all go on holiday somewhere very cold"))


if __name__ == '__main__':
    main()
