#! /bin/bash

if [ $# -ne 1 ]
then
    echo "Usage: $0 <hostfile>"
    exit 1;
fi

DIR=`dirname $0`
i=0

cat $1 | while read -r;
do
    echo $REPLY
    host=${REPLY%:*}
    port=`echo $REPLY | sed 's/.*\://g'`
    ssh -oStrictHostKeyChecking=no ${host} "cd ${PWD}; sh ${DIR}/worker -p ${port} > worker${i}.log" &
    i=$(($i+1))
done

sleep 2s

sh ${DIR}/master -h $1 > master.log &

echo "Please start the client at your leisure."
