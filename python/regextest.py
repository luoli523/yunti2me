#!/usr/bin/env python

import re
x = "guru99, education is fun"
r1 = re.findall(r"^\w+", x)
print r1

print(re.split("\s", "we are splitting the words"))   #['we', 'are', 'splitting', 'the', 'words']
print(re.split("\s", "we are  splitting the words"))  #['we', 'are', '', 'splitting', 'the', 'words']
print(re.split("\s+", "we are  splitting the words")) #['we', 'are', 'splitting', 'the', 'words']


list = ["guru99 get", "guru99 give", "guru Selenium"]
for e in list:
    z = re.match("(g\w+)\W(g\w+)", e)
    if z:
        print z.groups()