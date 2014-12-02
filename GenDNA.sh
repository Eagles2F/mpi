#!/bin/sh

#Number of Points
b=10

#Number of Cluster
k=2


		echo ********GENERATING $b INPUT POINTS EACH IN $k CLUSTERS 
		python ./randomclustergen/DNAStrandGen.py -c $k  -p $b -o input/DNA.txt

