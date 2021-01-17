#/bin/sh
echo "begin"
dir=/Users/zqq/Downloads/zhong/31310002004/20200131
for file_old in `ls $dir`
do
  file_new=`echo $file_old | tr '[a-z]' '[A-Z]'`
  echo $file_new
  mv $dir/$file_old $dir/$file_new
done



#!/bin/bash

for date in {20201209..20201231}
do
    cp -r 20201206 $date
    for file_old in `ls $date|grep 20201206`
    do
        if test -f $file_old
        then
        file_new=`echo "$file_old"|sed "s/20201206/$date/g"`
        echo $file_new
        mv $date/$file_old $date/$file_new
        fi
    done
done

