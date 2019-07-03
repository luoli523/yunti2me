#!/usr/bin/env python
import csv
import pandas

with open("data.csv", "rt") as f:
    data = csv.reader(f)
    for row in data:
        print row


reader = csv.DictReader(open("data.csv"))
for raw in reader:
    print raw


result = pandas.read_csv('data.csv')
print result