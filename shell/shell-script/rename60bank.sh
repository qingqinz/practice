#!/bin/bash

for i in {1..60}
do
    for file_old in /data/dfs01/checkdata/ftpperfdata/wangchunyu00$i
    do file_new=`echo "$file_old"|sed 's/20200131/20200531/g'`
        echo $file_new
        file_new_1=`echo "$file_new"|sed "s/31310002004/wangchunyu00$i/g"`
        mv $file_old $file_new_1
    done
done