#! /bin/bash

if [ $# -ne 1 ]
then
    echo "Usage: $0 <hostfile>"
    exit 1;
fi

if [ ! -d logs ]
then
    mkdir logs
fi
if [ ! -d interleave ]
then
    mkdir interleave
fi
if [ ! -d count ]
then
    mkdir count
fi

DIR=`dirname $0`
i=0

cat $1 | while read -r;
do
    echo "Starting a worker on: $REPLY"
    host=${REPLY%:*}
    port=`echo $REPLY | sed 's/.*\://g'`
    ssh -oStrictHostKeyChecking=no ${host} "cd ${PWD}; sh ${DIR}/worker -p ${port} > logs/worker${i}.log" &
    i=$(($i+1))
done

echo "Waiting 5s to start master to ensure workers are up..."

sleep 5s

sh ${DIR}/master -h $1 > logs/master.log &

echo "Please start the client at your leisure."
echo "./client [-t <tracefile>]"
