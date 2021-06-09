#!/bin/bash

for i in {1..10}
do
dd if=/dev/zero of=${i}M.txt count=$i bs=1024000
touch ${i}M.log
done