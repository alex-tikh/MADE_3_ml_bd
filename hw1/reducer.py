#!/usr/bin/env python3
"""reducer.py"""

import sys

count = 0
mean = 0
variance = 0

# input comes from STDIN
for line in sys.stdin:
    # remove leading and trailing whitespace
    line = line.strip()

    # parse the input we got from mapper.py
    ck, mk, vk = line.split('\t')

    # convert count (currently a string) to int
    try:
        ck = int(ck)
        mk = float(mk)
        vk = float(vk)
    except ValueError:
        # count was not a number, so silently
        # ignore/discard this line
        continue

    variance = (count*variance + ck*vk) / (count + ck) + count*ck*((mean - mk) / (count + ck))**2
    mean = (count*mean + ck*mk) / (count + ck)
    count += ck

print("{}\t{}\t{}".format(count, mean, variance))

