#!/bin/bash

echo "seq"
time ./kmeansPoints.py $@
echo "1"
time mpirun -np 1 ./pointsDist.py $@
echo "2"
time mpirun -np 2 ./pointsDist.py $@
echo "4"
time mpirun -np 4 ./pointsDist.py $@
echo "8"
time mpirun -np 8 ./pointsDist.py $@
echo "12"
time mpirun -np 12 ./pointsDist.py $@
