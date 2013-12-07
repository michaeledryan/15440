#!/bin/bash

echo "seq"
time ./kmeansDNA.py $@
echo "1"
time mpirun -np 1 ./dnaDist.py $@
echo "2"
time mpirun -np 2 ./dnaDist.py $@
echo "4"
time mpirun -np 4 ./dnaDist.py $@
echo "8"
time mpirun -np 8 ./dnaDist.py $@
echo "12"
time mpirun -np 12 ./dnaDist.py $@
