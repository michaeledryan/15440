#!/usr/bin/env python

import sys
import csv
import numpy as np
import getopt
import math
import random

def usage():
  print '$> ./generatePoints.py <required args> [optional args]\n' +\
    '\t-c <#>\t\tNumber of clusters to generate\n' + \
    '\t-p <#>\t\tNumber of iterations\n' + \
    '\t-o <file>\tData output location\n' + \
    '\t-i <file>\tData input location\n'


# Find distance between two points
def distance(p1, p2):
  return math.sqrt(math.pow((p2[0] - p1[0]), 2) + \
                  math.pow((p2[1] - p1[1]), 2))


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

numClusters, numIters, outfile, infile = handleArgs(sys.argv)

datapoints = []

# parse input file
with open(infile, 'rb') as csvfile:
  inreader = csv.reader(csvfile, delimiter=',')
  for row in inreader:
    datapoints.append((float(row[0]),float(row[1])))

# generate initial centroid list
centroids = {x: []  for x in random.sample(datapoints, numClusters)}

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
    newX = np.mean([x[0] for x in points])
    newY = np.mean([y[1] for y in points])
    newCentroids[(newX, newY)] = []

  #save old means as well
  oldCentroids = centroids
  centroids = newCentroids

# results
writer = csv.writer(open(outfile, "w"))
for centroid in oldCentroids:
  writer.writerow([centroid[0], centroid[1]])
