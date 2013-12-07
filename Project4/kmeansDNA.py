#!/usr/bin/env python

import sys
import csv
import numpy as np
import getopt
import math
import random
from collections import Counter

def usage():
  print '$> ./generatePoints.py <required args>\n' +\
    '\t-c <#>\t\tNumber of clusters to generate\n' + \
    '\t-p <#>\t\tNumber of iterations\n' + \
    '\t-o <file>\tData output location\n' + \
    '\t-i <file>\tData input location\n'


# Find closeness between two points
def closeness(s1, s2):
  dist = 0
  for i in xrange(min(len(s1), len(s2))):
    if (s1[i] != s2[i]):
      dist += 1
  return min(len(s1), len(s2)) - dist


# Parse out arguments
def handleArgs(args):
  numClusters = -1
  numIters = -1
  outfile = None
  infile = None

  try:
    optlist, args = getopt.getopt(args[1:], 'c:p:o:i:')
  except getopt.GetoptError, err:
    print str(err)
    usage()
    sys.exit(2)

  for key, val in optlist:
    if   key == '-c':
        numClusters = int(val)
    elif key == '-p':
        numIters = int(val)
    elif key == '-o':
        outfile = val
    elif key == '-i':
        infile = val

  if numClusters < 0 or numIters < 0 or \
    infile is None or outfile is None:
    usage()
    sys.exit()
  return (numClusters, numIters, outfile, infile)

# Find the average of multiple strands. Assuming each strand is
# the same length, take the mode of the char at each index.
def meanStrand(strands):
  if (len(strands) == 0):
    return None;
  newStrand = []
  for i in xrange(len(strands[0])):
    data = Counter([x[i] for x in strands])
    newStrand.append(data.most_common(1)[0][0])
  return "".join(newStrand)

numClusters, numIters, outfile, infile = handleArgs(sys.argv)

datapoints = []
# parse input file
with open(infile, 'rb') as csvfile:
  inreader = csv.reader(csvfile)
  for row in inreader:
    datapoints.append(row[0])

# generate initial centroid list
centroids = { x:[] for x in random.sample(datapoints, numClusters)}
for i in xrange(numIters):

  # put each point in a cluster with the nearest mean
  for point in datapoints:
    myKeys = centroids.keys()
    dists = [closeness(point, x) for x in myKeys]
    centroids[myKeys[np.argmax(dists)]].append(point)

  # Calculate new means from the clusters
  newCentroids = {}
  for center in centroids.keys():
    points = centroids[center]
    newCentroids[meanStrand(points)] = []

  #save old means as well
  oldCentroids = centroids
  centroids = newCentroids

# results
writer = csv.writer(open(outfile, "w"))
for centroid in oldCentroids:
  writer.writerow([centroid,])
