#!/usr/bin/env python

import sys
import csv
import numpy as np
import getopt
import math
import random
from mpi4py import MPI
import cPickle as pickle

comm = MPI.COMM_WORLD
rank = comm.Get_rank()
size = comm.Get_size()

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

datapoints = datapoints[rank * (len(datapoints) / size):(rank+1) *
                        (len(datapoints) / size)]

centroidMap = [[]] * size
centroids = {}
# generate initial centroid list
if rank == 0:
  centroidsInit = random.sample(datapoints, numClusters)
  for i, c in enumerate(centroidsInit):
    idx = i % size
    centroidMap[idx].append(c)
    centroids[c] = []

centroidMap = comm.bcast(centroidMap, root=0)
centroids = comm.bcast(centroids, root=0)

for it in xrange(numIters):
  print rank, it, "A"

  # put each point in a cluster with the nearest mean
  for point in datapoints:
    myKeys = centroids.keys()
    dists = [distance(point, x) for x in myKeys]
    centroids[myKeys[np.argmin(dists)]].append(point)

  print rank, it, "B"

  for i, cs in enumerate(centroidMap):
    print ">", i, rank
    if i != rank:
      toSend = {}
      for c in cs:
        toSend[c] = centroids[c]
      print "send:", rank, "->", i
      comm.isend(pickle.dumps(toSend), dest=i, tag=1)

  print rank, it, "C"

  newpoints = []
  for c in centroidMap[rank]:
    newpoints += centroids[c]

  print rank, it, "D"

  comm.Barrier()

  for i in xrange(size-1):
    data = pickle.loads(comm.recv(tag=1))
    print "recv:", rank
    for k in data:
      v = data[k]
      #print k, v
      newpoints += v
      centroids[k] += v

  print rank, it, "E"

  datapoints = newpoints

  # Calculate new means from the clusters
  newCentroids = {}
  for center in centroidMap[rank]:
    points = centroids[center]
    newX = np.mean([x[0] for x in points])
    newY = np.mean([y[1] for y in points])
    newCentroids[(newX, newY)] = []

  print rank, it, "F"

  for i in xrange(size):
    if i != rank:
      comm.isend(newCentroids.keys(), dest=i, tag=2)

  centroidMap[rank] = newCentroids.keys()

  print rank, it, "G"

  comm.Barrier()

  for i in xrange(size):
    if i != rank:
      data = comm.recv(source=i, tag=2)
      centroidMap[i] = data
      for c in data:
        newCentroids[c] = []

  print rank, it, "H"

  #save old means as well
  oldCentroids = centroids
  centroids = newCentroids

# results
if rank == 0:
  for i in xrange(size-1):
    data = comm.recv(tag=3)
    for k, v in data:
      oldCentroids[k] = v
else:
  toSend = {}
  for c in centroidMap[rank]:
    toSend[c] = oldCentroids[c]
  comm.isend(toSend, dest=0, tag=3)

writer = csv.writer(open(outfile, "w"))
for centroid in oldCentroids:
  writer.writerow([centroid[0], centroid[1]])
