#!/bin/bash

DIR=$(dirname $0)
JAR=MapReduce.jar
EXECUTABLE=$DIR/../build/libs/$JAR

if [ $# -lt 1 ]
then
    echo "Usage: $0 <-l> <-c [config]>"
    exit 1;
fi

if [ -f $EXECUTABLE ]; then
    java -cp $EXECUTABLE mikereduce.jobtracker.client.JobClient $@
    exit 0
else
    echo "JAR not found: " $EXECUTABLE
    echo "Please build project."
    exit 1
fi