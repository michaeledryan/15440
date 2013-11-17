#!/bin/bash

cd $(dirname $0)/..

javadoc -d doc -noqualifier java.lang:java.io -classpath src/main/java:build/libs/MapReduce.jar src/main/java/AFS/DistributedIO.java src/main/java/AFS/Connection.java
