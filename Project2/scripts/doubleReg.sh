#! /bin/bash

DIR=$(dirname $0)
LOGS=$DIR/../logs
JAR=RMI.jar
EXECUTABLE=$DIR/../build/libs/$JAR

if [ $# -ne 1 ]
then
    echo "Usage: $0 <testname>"
    echo "Only use this script for tests needing a two registries."
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
java -cp $EXECUTABLE registry.RegistryImpl > $LOGS/registry1_stdout.log 2> registry1_stderr.log &
java -cp $EXECUTABLE registry.RegistryImpl -p 8001 > $LOGS/registry2_stdout.log 2> registry2_stderr.log &
sleep 1
echo "Starting server. Logs are in logs/."
java -cp $EXECUTABLE tests/TestServer -t $1 > $LOGS/server1_stdout.log 2> server1_stderr.log &
java -cp $EXECUTABLE tests/TestServer -p 1100 -rp 8001 -t $1 > $LOGS/server2_stdout.log 2> server2_stderr.log &
sleep 1
echo "Starting client. Output to stdout/stderr."
$DIR/../bin/client -r localhost -p 8000 -r localhost -p 8001 -t $1

echo "Finished!"
echo "Killing registries..."
kill `jps | grep "RegistryImpl" | awk {'print $1'}`
echo "Killing servers..."
kill `jps | grep "TestServer" | awk {'print $1'}`
