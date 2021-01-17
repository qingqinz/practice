#!/bin/bash
for file in /data/dfs01/east/ftphome/B0213H251110001/20200430/*
do
  file_new_1=`echo "$file"|sed 's/.txt/.log/g'`
  touch $file_new_1
  done