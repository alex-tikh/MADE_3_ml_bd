#!/usr/bin/env python3
"""mapper.py"""

import sys

ck = 0
mk = 0
vk = 0
price = 0
price_sq = 0
# input comes from STDIN (standard input)
for line in sys.stdin:
    try:
        #print(line)
        x = float(line)
        price += x
        ck += 1
        price_sq += x ** 2
    except ValueError:
        continue

mk = price / ck
vk = (price_sq - price ** 2 / ck) / (ck)

print("{}\t{}\t{}".format(ck, mk, vk))

