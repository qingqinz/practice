#!/bin/sh

for tablename in `cat hivetable`
do
echo $tablename
hive -e 'show partitions checkone.'$tablename';'>tmp.log
for line in `cat tmp.log`
do
 if [[ $line =~ __ ]];then
  echo $line
  array=(${line//// })
  orgno=${array[0]}
  orgno=${orgno:7}
  echo $orgno
  loaddate=${array[1]}
  loaddate=${loaddate:10}
  echo $loaddate
  hive -e 'ALTER TABLE checkone.'$tablename' DROP IF EXISTS PARTITION (org_no ="'$orgno'",load_date="'$loaddate'");'
 fi
done
done