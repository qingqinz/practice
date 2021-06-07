#!/bin/bash

JAR_NAME=$1
SERVER_PORT=$2

if [[ ! ${JAR_NAME} ]] ; then
    echo "please input jar name "
    exit -1
fi

if [ ! -f $JAR_NAME ]; then
   echo "$JAR_NAME not exist"
   exit -1
fi

if [[ ! $SERVER_PORT ]] ; then
    SERVER_PORT=7100
fi

./stop.sh $SERVER_PORT

./start.sh $JAR_NAME $SERVER_PORT
