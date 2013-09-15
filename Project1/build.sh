#! /bin/bash

foo=`which gradle`

if [ 0 -ne $? ]
then
    GRADLE=/afs/andrew.cmu.edu/usr13/acappiel/public/bin/gradle-1.7/bin/gradle
else
    GRADLE=gradle
fi

$GRADLE assemble
