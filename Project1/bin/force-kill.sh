#! /bin/bash

if [ $# -ne 1 ]
then
    echo "Usage: $0 <hostfile>"
    exit 1;
fi

DIR=`dirname $0`

cat $1 | while read -r;
do
    echo $REPLY
    host=${REPLY%:*}
    ssh -oStrictHostKeyChecking=no ${host} "kill \`jps | grep \"Main\" | awk {'print \$1'}\`" &
done

#kill `jps | grep "Main" | awk {'print $1'}`
