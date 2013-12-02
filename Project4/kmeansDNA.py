#!/usr/local/bin/python

import sys
import csv
import numpy as np
import getopt
import math
import random
from collections import Counter

def usage():
  print '$> ./generatePoints.py <required args> [optional args]\n' +\
    '\t-c <#>\t\tNumber of clusters to generate\n' + \
    '\t-p <#>\t\tNumber of iterations\n' + \
    '\t-o <file>\t\tData output location\n' + \
    '\t-i <file>\t\tData input location\n'


# Find distance between two points
def distance(s1, s2):
  dist = 0
  for i in xrange(min(len(s1), len(s2))):
    if (s1[i] != s2[i]):
      dist += 1
  return min(len(s1), len(s2)) - dist

def handleArgs(args):
    # Parse out arguments
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
      # first, the required arguments
      if   key == '-c':
          numClusters = int(val)
      elif key == '-p':
          numIters = int(val)
      elif key == '-o':
          outfile = val
      # now, the optional argument
      elif key == '-i':
          infile = val

    if numClusters < 0 or numIters < 0 or \
      infile is None or outfile is None:
      usage()
      sys.exit()
    return (numClusters, numIters, outfile, infile)


def meanStrand(strands):
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
    dists = [distance(point, x) for x in myKeys]
    centroids[myKeys[np.argmin(dists)]].append(point)
  
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