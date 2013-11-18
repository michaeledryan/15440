#! /bin/bash

if [ $# -ne 2 ]
then
    echo "Usage: $0 <hostfile> <jobtrackerconf>"
    exit 1;
fi

if [ ! -d logs ]
then
    mkdir logs
else
    rm -rf logs/*
fi

HOST=`hostname`
echo "[main]" > mapper-tmp.ini
echo "port=9002" >> mapper-tmp.ini
echo address=$HOST >> mapper-tmp.ini

DIR=`dirname $0`
i=0

echo "Starting JobTracker and name node."

sh ${DIR}/jobtracker -c $2 > logs/jobtracker.log &
sh ${DIR}/nameserver > logs/nameserver.log &

echo "Waiting 5s to start workers to ensure masters are up..."

sleep 5s

cat $1 | while read -r;
do
    echo "Starting a worker on: $REPLY"
    host=${REPLY%:*}
    port=`echo $REPLY | sed 's/.*\://g'`
    ssh -oStrictHostKeyChecking=no ${host} "cd ${PWD}; sh ${DIR}/dataserver -p ${port} > logs/dataserver${i}.log" &
    ssh -oStrictHostKeyChecking=no ${host} "cd ${PWD}; sh ${DIR}/worker -c mapper-tmp.ini > logs/worker${i}.log" &
    i=$(($i+1))
done

