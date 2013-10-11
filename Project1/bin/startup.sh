#!/bin/bash

DIR=$(pwd)


ssh localhost "cd $DIR; sh worker -p 9001 > worker1.log" &
ssh localhost "cd $DIR; sh worker -p 9002 > worker2.log" &
ssh localhost "cd $DIR; sh master -h zz.txt > worker3.log" &

echo "Please start the client at your leisure."