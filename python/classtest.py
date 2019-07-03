#!/usr/bin/env python

class myClass():
    def func1(self):
        print("myClass.func1()")

    def func2(self, sth):
        print("myClass.func2(sth): " + sth)

class myClass2:
    name = ""

    def __init__(self, name):
        self.name = name

    def func1(self):
        print "myClass2 name: " + self.name

def main():
    c = myClass()
    c.func1()
    c.func2("luoli")

    d = myClass2("luoli")
    d.func1()

if __name__ == "__main__":
    main()