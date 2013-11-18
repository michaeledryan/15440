#!/bin/bash

cd $(dirname $0)/..

javadoc -d doc -noqualifier java.lang:java.io -classpath src/main/java:build/libs/MapReduce.jar src/main/java/AFS/DistributedIO.java src/main/java/AFS/Connection.java src/main/java/mikereduce/jobtracker/shared/Reducer.java src/main/java/mikereduce/shared/Mapper.java src/main/java/mikereduce/shared/InputFormat.java src/main/java/mikereduce/shared/OutputFormat.java
