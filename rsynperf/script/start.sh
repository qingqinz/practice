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

nohup java -jar -Xmx4096m -Xms4096m -XX:+PrintGCDetails -Xloggc:gc-`date +%Y%m%d%H`.log -XX:+HeapDumpOnOutOfMemoryError -Dspring.config.location=./application-prod.properties ./$JAR_NAME --server.port=$SERVER_PORT  2>&1 >/dev/null &
echo 'start success...'
