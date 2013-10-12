#! /bin/bash

DIR=$(dirname $0)
LOGS=$DIR/../logs
JAR=RMI.jar
EXECUTABLE=$DIR/../build/libs/$JAR

if [ $# -ne 1 ]
then
    echo "Usage: $0 <testname>"
    echo "Only use this script for tests needing a single registry."
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

echo "Starting registry. Logs are in logs/."
java -cp $EXECUTABLE registry.RegistryImpl > $LOGS/registry_stdout.log 2> $LOGS/registry_stderr.log &
sleep 1
echo "Starting server. Logs are in logs/."
java -cp $EXECUTABLE tests/TestServer -t $1 > $LOGS/server_stdout.log 2> $LOGS/server_stderr.log &
sleep 1
echo "Starting client. Output to stdout/stderr."
$DIR/../bin/client -t $1

echo "Finished!"
echo "Killing registry..."
kill `jps | grep "RegistryImpl" | awk {'print $1'}`
echo "Killing server..."
kill `jps | grep "TestServer" | awk {'print $1'}`
