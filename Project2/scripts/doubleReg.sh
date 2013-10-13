#! /bin/bash

DIR=$(dirname $0)
LOGS=$DIR/../logs
JAR=RMI.jar
EXECUTABLE=$DIR/../build/libs/$JAR

if [ $# -ne 1 ]
then
    echo "Usage: $0 <testname>"
    echo "Only use this script for tests needing a two registries."
    echo "Client runs <testname>. Server1 runs <testname>. Server2 runs <testname>b."
    echo "All processes are on localhost."
    exit 1
fi

if [ ! -f $EXECUTABLE ]
then
    echo "JAR not found: " $EXECUTABLE
    echo "Please bulid project."
    exit 1
fi

if [ ! -d $LOGS ]
then
    mkdir $LOGS
fi

rm $LOGS/*.log

echo "Starting registries. Logs are in logs/."
java -cp $EXECUTABLE registry.RegistryImpl > $LOGS/registry1_stdout.log 2> $LOGS/registry1_stderr.log &
java -cp $EXECUTABLE registry.RegistryImpl -p 8001 > $LOGS/registry2_stdout.log 2> $LOGS/registry2_stderr.log &
sleep 1
echo "Starting servers. Logs are in logs/."
java -cp $EXECUTABLE tests/TestServer -t $1 > $LOGS/server1_stdout.log 2> $LOGS/server1_stderr.log &
java -cp $EXECUTABLE tests/TestServer -p 1100 -rp 8001 -t $1b > $LOGS/server2_stdout.log 2> $LOGS/server2_stderr.log &
sleep 1
echo "Starting client. Output to stdout/stderr."
java -cp $EXECUTABLE tests/TestClient -r localhost -p 8000 -r localhost -p 8001 -t $1

echo "Finished!"
echo "Killing registries..."
kill `jps | grep "RegistryImpl" | awk {'print $1'}`
echo "Killing servers..."
kill `jps | grep "TestServer" | awk {'print $1'}`
