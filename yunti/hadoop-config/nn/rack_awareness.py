#!/bin/env python
import re
import sys

def get_racks(hosts):
    racks = []
    for host in hosts:
       rack = get_rack(host)
       racks.append(rack)

    return racks

def get_rack(host):
    rack = "/default-rack"
    try:
        f = open("/etc/hosts")
        for line in f:
            parts = line.split()
            for part in parts:
                if part == host:
                    mr = re.match("^(r[0-9]{2}[a-z][0-9]{2})[0-9]{3}.*$", parts[1])
                    if mr != None:
                        rack = "/%s" % mr.group(1)
                        return rack
    except:
        pass

    return rack 
        
def replace_all(text, dic):
    for i, j in dic.iteritems():
        text = text.replace(i, j)
    return text

if len(sys.argv) < 2:
    print >>sys.stderr, "usage: %s host1 host2 ..." % sys.argv[0] 
    sys.exit(1)
else:
    # dictionary with key:values. we want to remove ',', '[', ']', '''
    reps = {',':'', '[':'', ']':'', '\'':''}
    hosts = sys.argv[1:]
    for i in range(0, len(hosts)):
       # the input from nn is like 10.250.8.60, 10.250.8.61, ...
       hosts[i] = replace_all(hosts[i], reps)

    racks = get_racks(hosts)
    rack_str = str(racks)
    # return format: /r01h011 /r02j012 /default-rack ...
    print replace_all(rack_str, reps)
    sys.exit(0)
