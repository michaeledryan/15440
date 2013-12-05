#!/usr/bin/env python

import sys
import csv
import numpy as np
import getopt
import math
import random
from collections import Counter
from mpi4py import MPI

def usage():
  print '$> ./generatePoints.py <required args>\n' +\
    '\t-c <#>\t\tNumber of clusters to generate\n' + \
    '\t-p <#>\t\tNumber of iterations\n' + \
    '\t-o <file>\tData output location\n' + \
    '\t-i <file>\tData input location\n'


# Find distance between two points
def distance(s1, s2):
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

length = len(datapoints[0])

datapoints = np.array(datapoints)

comm = MPI.COMM_WORLD
rank = comm.Get_rank()

# generate initial centroid list
if rank == 0:
  centroids = { x:[] for x in random.sample(datapoints, numClusters)}
  keys = centroids.keys()
  for strand in datapoints:
    dists = np.array([distance(strand, x) for x in keys])
    centroids[keys[np.argmin(dists)]].append(strand)
  data = [(x, centroids[x]) for x in centroids.keys()]
else:
  data = None
data = comm.bcast(data, root=0)

myCentroid = data[rank][0]
datapoints = data[rank][1]

centroids = {x[0]: [] for x in data}
centroidRanks = {data[i][0]: i for i in xrange(len(data))}

for i in xrange(numIters):

  print rank, i, centroids

  # put each point in a cluster with the nearest mean
  for point in datapoints:
    myKeys = centroids.keys()
    dists = np.array([distance(point, x) for x in myKeys])
    centroids[myKeys[np.argmin(dists)]].append(point)

  datapoints = np.array(centroids[myCentroid])
  sendLengths = [0] * len(centroidRanks.keys())

  means = centroidRanks.keys()
  for mean in means:
    if mean != myCentroid:
      comm.isend(length * len(centroids[mean]), dest=centroidRanks[mean], tag=0)

  for mean in means:
    if mean != myCentroid:
      myData = comm.recv(source=centroidRanks[mean], tag=0)
      sendLengths[centroidRanks[mean]] = myData

  for mean in means:
    if mean != myCentroid:
      dataToSend = np.array(centroids[mean])
      print "a", dataToSend
      comm.Isend([dataToSend, MPI.CHAR], dest=centroidRanks[mean], tag=1)

  for mean in means:
    if mean != myCentroid:
      myData = np.empty((sendLengths[centroidRanks[mean]], 1),
                        "|S%d" % (length))
      comm.Recv([myData, MPI.CHAR], source=centroidRanks[mean], tag=1)

      if (myData.size != 0):
        print "p", datapoints
        print "q", myData
        np.concatenate([datapoints, myData])

  oldCentroid = myCentroid
  myCentroid = meanStrand(datapoints)

  for mean in means:
    if mean != myCentroid:
      comm.isend(myCentroid, dest=centroidRanks[mean], tag=3)

  newCentroids = {myCentroid:[]}
  newCentroidRanks = {myCentroid:rank}
  for mean in means:
    if mean != myCentroid:
      myData = comm.recv(source=centroidRanks[mean], tag=3)
      newCentroids[myData] = []
      newCentroidRanks[myData] = centroidRanks[mean]

  centroidRanks = newCentroidRanks
  centroids = newCentroids

# results
#writer = csv.writer(open(outfile, "w"))
#for centroid in oldCentroids:
#  writer.writerow([centroid,])
