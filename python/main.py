#!/usr/bin/env python

def main():
    print "Hello, Python!"
    a = "Guru"
    b = 99
    print a + str(b)


if __name__ == "__main__":
    main()

print "Guru99!"

f = 99
print(f)

def someFunction():
    global f
    f = "changing global variable"
    print(f)

someFunction()
print(f)

var1 = "Guru99!"
var2 = "Software Testing"
f = 99
print("var1[0]:", var1[0])
print("var2[1:5]:", var2[1:5])

print("u" in var1)
print("r" not in var2)
print '%s is %d' % (var1, f)

x = "Hello World!"
print(x[:6])
print(x[0:6] + "Guru99")

str="12345"
print(''.join(reversed(str)))

tup1 = ('Robert', 'Carlos','1965','Terminator 1995', 'Actor','Florida')
tup2 = (1,2,3,4,5,6,7,8,9)
print(tup1[0])
print(tup2[1:5])

x = ("Guru99", 10, "Education")  # tuple packing
(company, emp, profile) = x  # unpacking tuple
print(company)
print(emp)
print(profile)

a = (5, 1)
b = (1, 4)
if (a > b):
    print("a is bigger")
else:
    print("b is bigger")

a = (5,6)
b = (5,6,1)
if (a > b):
    print("a is bigger")
else:
    print("b is bigger")
print("a == b? :", a == b)

a = (5,6)
b = (6,4)
if (a > b):
    print("a is bigger`")
else:
    print("b is bigger")

# directory
Dict = {'Tim': 18,
        'Charlie':12,
        'Tiffany':22,
        'Robert':25}
print(Dict['Tim'])
Dict.update({'Tim':25})
print(Dict['Tim'])
Dict.update({'LuoLi':37})
print(Dict['LuoLi'])
del Dict['LuoLi']
print Dict

print "Students Names: %s " % Dict.items()

Boys = {'Tim': 18,'Charlie':12,'Robert':25}
Girls = {'Tiffany':22}
for key in Dict.keys():
    if key in Boys.keys():
        print "%s is a boy." % key
    else:
        print "%s is a girl." % key

students = Dict.keys()
students.sort()
print students

print "type of Dict is: %s" %type(Dict)
