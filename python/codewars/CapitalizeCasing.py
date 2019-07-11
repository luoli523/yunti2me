#!/usr/bin/env python


def toJadenCase(string):
    w = [w.capitalize() for w in string.split()]
    return ' '.join(w)


def main():
    quote = "How can mirrors be real if our eyes aren't real"
    print(toJadenCase(quote))


if __name__ == "__main__":
    main()