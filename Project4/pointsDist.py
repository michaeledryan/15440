#!/usr/bin/env python

import sys
import csv
import numpy as np
import getopt
import math
import random
from mpi4py import MPI

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
datapoints = np.array(datapoints)

comm = MPI.COMM_WORLD
rank = comm.Get_rank()
if rank == 0:
  centroids = {(x[0], x[1]): []  for x in random.sample(datapoints, numClusters)}
  myKeys = centroids.keys()
  for point in datapoints:  
    dists = np.array([distance(point, x) for x in myKeys])
    centroids[myKeys[np.argmin(dists)]].append((point[0], point[1]))
  data = [(x, centroids[x]) for x in centroids.keys()]
else:
  data = None
data = comm.bcast(data, root=0)

myCentroid = data[rank][0]
datapoints = data[rank][1]

print "Initial comms done, get on rank %d with centroid:"%rank, myCentroid

centroids = {x[0]: [] for x in data}

centroidRanks = {data[i][0]: i for i in xrange(len(data))}
for i in xrange(numIters):

  # Reshuffle the closeness.
  for point in datapoints:
    myKeys = centroidRanks.keys()
    dists = np.array([distance(point, x) for x in myKeys])
    centroids[myKeys[np.argmin(dists)]].append(point)

  datapoints = np.array(centroids[myCentroid])
  sendLengths = [0] * len(centroidRanks.keys())

  # Send updates

  means = centroidRanks.keys()
  for mean in means:
    if (mean != myCentroid):
      comm.isend(len(centroids[mean]), dest=centroidRanks[mean], tag=0)
  
  for mean in means:
    if (mean != myCentroid):
      myData = comm.recv(source=centroidRanks[mean], tag=0)
      sendLengths[centroidRanks[mean]] = myData
#      print "rank", rank, "got length:", sendLengths[centroidRanks[mean]]

#  print "RECEIVED LENGTHS"

  # Send updates
  means = centroidRanks.keys()
  for mean in means:
    if (mean != myCentroid):

      dataToSend = np.array(centroids[mean])
#      print "OMG SO DATA:", dataToSend.dtype
#      print "rank", rank, "sending array of length", len(centroids[mean])
      comm.Isend([dataToSend, MPI.FLOAT], dest=centroidRanks[mean], tag=1)
  
  for mean in means:
    if (mean != myCentroid):
      #myData = np.zeros(sendLengths[centroidRanks[mean]] * 2, 'f')
      #myData = np.zeros(10000, 'float32, float32')
      myData = np.empty((sendLengths[centroidRanks[mean]], 2), 'float64')

      #print "myData", myData
      #print "rank", rank, "expecting length", sendLengths[centroidRanks[mean]]
      comm.Recv([myData, MPI.FLOAT], source=centroidRanks[mean], tag=1)
      
      if (myData.size != 0):
        np.concatenate([datapoints, myData])

  # Calculate new mean from the clusters
  newMean = {}
  newX = np.mean([x[0] for x in datapoints])
  newY = np.mean([y[1] for y in datapoints])
  
  #save old means as well
  oldCentroid = myCentroid
  myCentroid = (newX, newY)

  # Send updates
  means = centroidRanks.keys()
  for mean in means:
    if (mean != myCentroid):
      comm.isend(myCentroid, dest=centroidRanks[mean], tag=3)

  
  newCentroids = {myCentroid:[]}
  newCentroidRanks = {myCentroid:rank}
  for mean in means:
    if (mean != myCentroid):
      myData = comm.recv(source=centroidRanks[mean], tag=3)
      newCentroids[myData] = []
      newCentroidRanks[myData] = centroidRanks[mean]

  centroidRanks = newCentroidRanks
  centroids = newCentroids

print rank, myCentroid

# results
'''
if rank == 0:
  writer = csv.writer(open(outfile, "w"))
  for centroid in oldCentroids:
    writer.writerow([centroid[0], centroid[1]])
'''