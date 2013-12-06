#!/usr/bin/env python

import sys
import csv
import numpy as np
import getopt
import math
import random
import hashlib
import os
from mpi4py import MPI

def usage():
  #print '$> ./generatePoints.py <required args> [optional args]\n' +\
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
      #print str(err)
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

# Hash a centroid to allow for tagging
def effectiveHash(mean):
  hashVal = 13
  hashVal = hashVal * 31 + hash(str(mean[0]))
  hashVal = hashVal * 31 + hash(str(mean[1]))
  return hashVal % (10**8)

# Parse args
numClusters, numIters, outfile, infile = handleArgs(sys.argv)


comm = MPI.COMM_WORLD
rank = comm.Get_rank()


datapoints = []
# parse input file
with open(infile, 'rb') as csvfile:
  inreader = csv.reader(csvfile, delimiter=',')
  for row in inreader:
    datapoints.append((float(row[0]),float(row[1])))

# Generate initial centroid list
datapoints = np.array(datapoints)

if rank == 0:
  # Clear output file
  os.remove(outfile)

  # Pick random initial means
  centroids = {(x[0], x[1]): []  for x in random.sample(datapoints, numClusters)}
  myKeys = centroids.keys()

  # Assign points to their clusters
  for point in datapoints:  
    dists = np.array([distance(point, x) for x in myKeys])
    centroids[myKeys[np.argmin(dists)]].append(point)

  data = [(x, centroids[x]) for x in centroids.keys()]
else:
  data = None

# Send initital centroids and cluster points
data = comm.bcast(data, root=0)

# my centroids and datapoints for each one
myCentroids = [data[i][0] for i in xrange(rank, numClusters, comm.size)]
myDataPoints = [np.array(data[i][1]) for i in xrange(rank, numClusters, comm.size)]

# global centroids
centroids = {x[0]: [] for x in data}

# Map means to their ranks
centroidRanks = {data[i][0]: i % comm.size for i in xrange(len(data))}

for i in xrange(numIters):

  updatesToSend = [[]] * comm.size;  
  receivedUpdates = [[]] * comm.size;

  print rank, "has tags", [effectiveHash(x) for x in myCentroids]
  
  # Reshuffle the closeness.
  for point in datapoints:
    myKeys = centroidRanks.keys()
    dists = np.array([distance(point, x) for x in myKeys])
    centroids[myKeys[np.argmin(dists)]].append(point)

  # Reassign your own datapoints
  for i in xrange(len(myCentroids)):
    myDataPoints[i] = centroids[myCentroids[i]]
  
  # For each centroid, note how many points must be sent to their node
  means = centroidRanks.keys()
  for mean in means:
    if (mean not in myCentroids):
      rnk = centroidRanks[mean]
      updateSize = len(centroids[mean])
      updatesToSend[rnk].append((mean, updateSize))

  # Send a list of (centroid, numUpdates) pairs to each other node
  for i in xrange(len(updatesToSend)):
    if (i != rank):
      comm.isend(updatesToSend[i], dest=i, tag=7)
  
  # Receive a list of updates from each other node
  for i in xrange(len(receivedUpdates)):
    if (i != rank):
      myData = comm.recv(source=i, tag=7)
      receivedUpdates[i] = myData
  
  # Send actual updated lists of points. Use hash of the centroid as tag
  means = centroidRanks.keys()
  for mean in means:
    if (mean not in myCentroids):
      dataToSend = np.array(centroids[mean])
      comm.Isend([dataToSend, MPI.FLOAT], dest=centroidRanks[mean], \
        tag=effectiveHash(mean))
  
  # Iterate through received data, going through every received list of tuples
  for k in xrange(len(receivedUpdates)):  
    updateList = receivedUpdates[k]
    
    # All updates from a given node
    for i in xrange(len(updateList)):  
      if (i != rank):
        updateLength = updateList[i][1]
        updateMean = updateList[i][0]
        updateHash = effectiveHash(updateMean)
        # Don't look for junk
        if (updateMean not in myCentroids):
          continue

        # Receive new list of points
        myData = np.empty((updateLength, 2), 'float64')          
        comm.Recv([myData, MPI.FLOAT], source=k, tag=updateHash)
      
        if (myData.size != 0):
          # update my centroid
          j = myCentroids.index(updateMean)
          myDataPoints[j] = np.array(myDataPoints[j])
          np.concatenate([myDataPoints[j], np.array(myData, ndmin=2)])


  # Save current means
  oldCentroids = myCentroids

  # Calculate new mean from the clusters
  for i in xrange(len(myCentroids)):
    newX = np.mean([x[0] for x in myDataPoints[i]])
    newY = np.mean([y[1] for y in myDataPoints[i]])
    myCentroids[i] = (newX, newY)
    

  # Send updates
  means = centroidRanks.keys()
  for mean in means:
    if (mean not in myCentroids):
      comm.isend(myCentroids, dest=(centroidRanks[mean] % comm.size), tag=3)

  # repopulate centroids and their ranks
  newCentroids = {x : [] for x in myCentroids}
  newCentroidRanks = {x : rank for x in myCentroids}
  for mean in means:
    if (mean not in myCentroids):
      myData = comm.recv(source=(centroidRanks[mean] % comm.size), tag=3)

      for datum in myData:
        newCentroids[datum] = []
        newCentroidRanks[datum] = centroidRanks[mean]

  centroidRanks = newCentroidRanks
  centroids = newCentroids

# Just append.
writer = csv.writer(open(outfile, "a"))
for centroid in oldCentroids:
  writer.writerow([centroid[0], centroid[1]])
