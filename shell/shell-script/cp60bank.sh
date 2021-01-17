#!/bin/bash

for i in {1..60}
do
    for file in /data/dfs01/checkdata/ftpperfdata/*
    do
        if test -f $file
        then
        cp $file  /data/dfs01/checkdata/ftphome/wangchunyu00$i/20200531
        fi
    done
done


