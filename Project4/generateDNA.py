#!/usr/local/bin/python

import sys
import csv
import numpy
import getopt
import math

DNACHARS = ["a", "c", "t", "g"]

def usage():
  print '$> ./generateDNA.py <required args> [optional args]\n' +\
    '\t-n <#>\t\tNumber of strands to generate\n' + \
    '\t-l <#>\t\tLength per strand\n' + \
    '\t-o <file>\t\tData output location\n'

# Find difference between two DNA strands
def distance(s1, s2):
  dist = 0
  for i in xrange(math.min(len(s1), len(s2))):
    if (s1[i] != s2[i]):
      dist += 1
  return math.min(len(s1), len(s2)) - dist

def drawOrigin(strandLength):
  return "".join(numpy.random.choice(DNACHARS, size=strandLength).tolist());

def tooClose(point, points, min):
  for pair in points:
    if distance(point, pair) < min:
      return True
  return False

def handleArgs(args):
    # Parse out arguments
    numStrands = -1
    maxLen = 10
    output = None

    try:
      optlist, args = getopt.getopt(args[1:], 'n:l:o:')
    except getopt.GetoptError, err:
      print str(err)
      usage()
      sys.exit(2)

    for key, val in optlist:
      # first, the required arguments
      if   key == '-n':
          numStrands = int(val)
      elif key == '-l':
          maxLen = int(val)
      elif key == '-o':
          output = val
      # now, the optional argument

    if numStrands < 0 or maxLen < 1 or output is None:
      usage()
      sys.exit()
    return (numStrands, output, maxLen)

numStrands, output, strandLength = handleArgs(sys.argv)

writer = csv.writer(open(output, "w"))
centroids = [drawOrigin(strandLength) for x in xrange(numStrands)]

for row in centroids:
  writer.writerow([row])