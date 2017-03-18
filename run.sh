#!/usr/bin/env bash
START=$(date +%s.%N)
echo $START > startTime.txt
java -jar kiwi-salesman-1.0-28b8cd9-all.jar $1
END=$(date +%s.%N)
DIFF=$(echo "$END - $START" | bc)
echo $DIFF
