#!/usr/local/bin/python

import sys
import csv
import numpy
import getopt
import math

def usage():
  print '$> ./generatePoints.py <required args> [optional args]\n' +\
    '\t-c <#>\t\tNumber of clusters to generate\n' + \
    '\t-p <#>\t\tNumber of points per cluster\n' + \
    '\t-o <file>\t\tData output location\n' + \
    '\t-v [#]\t\tCeiling on coordinate values\n'



# Find distance between two points
def distance(p1, p2):
  return math.sqrt(math.pow((p2[0] - p1[0]), 2) + \
                  math.pow((p2[1] - p1[1]), 2))

def drawOrigin(max):
  return numpy.random.uniform(0, max, 2);

def tooClose(point, points, min):
  for pair in points:
    if distance(point, pair) < min:
      return True
  return False

def handleArgs(args):
    # Parse out arguments
    numClusters = -1
    numPoints = -1
    output = None
    maxValue = 10

    try:
      optlist, args = getopt.getopt(args[1:], 'c:p:o:v:')
    except getopt.GetoptError, err:
      print str(err)
      usage()
      sys.exit(2)

    for key, val in optlist:
      # first, the required arguments
      if   key == '-c':
          numClusters = int(val)
      elif key == '-p':
          numPoints = int(val)
      elif key == '-o':
          output = val
      # now, the optional argument
      elif key == '-v':
          maxValue = float(val)

    if numClusters < 0 or numPoints < 0 or \
      maxValue < 1 or \
      output is None:
      usage()
      sys.exit()
    return (numClusters, numPoints, output, maxValue)


numClusters, numPoints, output, maxValue = handleArgs(sys.argv)

writer = csv.writer(open(output, "w"))

# For each cluster, generate a random center point
centroids = []
minDistance = 0
for i in xrange(0, numClusters):
  centroid = drawOrigin(maxValue)
  while (tooClose(centroid, centroids, minDistance)):
    centroid = drawOrigin(maxValue);
  centroids.append(centroid)

# Populate each cluster
minVariance = 0
maxVariance = 0.5
for i in xrange(0, numClusters):
  variance = numpy.random.uniform(minVariance, maxVariance)
  cluster = centroids[i]
  for j in xrange(0, numPoints):
      x, y = numpy.random.normal(cluster, variance)
      writer.writerow([x,y])