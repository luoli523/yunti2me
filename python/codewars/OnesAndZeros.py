#!/usr/bin/env python


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


def main():
    print(binary_array_to_number([0,0,0,1]))
    print(binary_array_to_number([0,0,1,0]))
    print(binary_array_to_number([1,1,1,1]))
    print(binary_array_to_number([0,1,1,0]))

    print(binary_array_to_number2([0,0,0,1]))
    print(binary_array_to_number2([0,0,1,0]))
    print(binary_array_to_number2([1,1,1,1]))
    print(binary_array_to_number2([0,1,1,0]))

    print(binary_array_to_number3([0,0,0,1]))
    print(binary_array_to_number3([0,0,1,0]))
    print(binary_array_to_number3([1,1,1,1]))
    print(binary_array_to_number3([0,1,1,0]))


if __name__ == '__main__':
    main()
