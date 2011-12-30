#!/usr/bin/env python

# Grayson Hansard, 2008
# This is a simple python script that I wrote as part of a system to
# use the number of git commits on a project as an incremental build number system.

import os, sys

if len(sys.argv) < 2:
	print "Usage git hash-from-commit-number <commit-number>"
	sys.exit()

f = os.popen('git rev-list --all --reverse')
number = int(sys.argv[1])-1 # Correction for 0-based list index
lines = f.readlines()
if number == 0 or number > len(lines):
	print "Commit number out of range"
	sys.exit()
print lines[number][:-1]

