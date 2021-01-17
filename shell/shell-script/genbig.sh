#!/bin/bash

srcdir=$1
dstdir=$2

if [ ! -d $dstdir ] 
  then
  echo "mkdir $dstdir"
  mkdir $dstdir
fi

for file in $srcdir/*
do
  if test -f $file
  then
  echo "cp $file"
  cp $file $dstdir/
  else
  echo "$file not file"
  fi
done


for i in {1..10}
do
  for file in $srcdir/*
  do
     if test -f $file
     then
     filename=$(basename $file)
     cat $file >>$dstdir/$filename
     fi
  done
done
  

