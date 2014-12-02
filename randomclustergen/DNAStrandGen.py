import sys
import csv
import numpy
import getopt
import math
from random import randrange

def usage():
    print '$> python DNAStrandGen.py <required args> [optional args]\n' + \
        '\t-c <#>\t\tNumber of clusters to generate\n' + \
        '\t-p <#>\t\tNumber of strands per cluster\n' + \
        '\t-o <file>\tFilename for the output of the raw data\n' + \
        '\t-v [#]\t\tLength value for strands\n'  

       
       

def strandDistance(s1, s2):
    '''
    Takes two DNA strands and computes the distance between them.
    '''
    dis = 0
    for i in range(0,len(s1)):
      if s1[i] != s2[i]:
        dis=dis+1
    return dis

def tooClose(strand, strands, minDist):
    '''
    Computes the distance between the strands and all strands
    in the list, and if any strands in the list are closer than minDist,
    this method returns true.
    '''
    for pair in strands:
      if strandDistance(strand, pair) < minDist:
        return True
    return False

def handleArgs(args):
    # set up return values
    numClusters = -1
    numstrands = -1
    output = None
    maxValue = 20

    try:
        optlist, args = getopt.getopt(args[1:], 'c:p:v:o:')
    except getopt.GetoptError, err:
        print str(err)
        usage()
        sys.exit(2)

    for key, val in optlist:
        # first, the required arguments
        if   key == '-c':
            numClusters = int(val)
        elif key == '-p':
            numstrands = int(val)
        elif key == '-o':
            output = val
        # now, the optional argument
        elif key == '-v':
            maxValue = float(val)

    # check required arguments were inputted  
    if numClusters < 0 or numstrands < 0 or \
            maxValue < 1 or \
            output is None:
        usage()
        sys.exit()
    return (numClusters, numstrands, output, \
            maxValue)

def drawDNAatDistance(distance,centroid):            
    '''
    Modify DNA strand in different positions,the number of changes is 'distance'
    '''    
    length = len(centroid)
    pos =[]
    for i in range(0,distance):
        temp = randrange(length)
        if temp not in pos:
            pos.append(temp)
    for p in pos:
        strand = adjustDNA(p,centroid)
    return strand

def adjustDNA(p,centroid):
    c = centroid[p]    
    temp = randrange(4)
    s=''
    if temp == 0:
      s='A'
    elif temp == 1:
      s='C'
    elif temp == 2:
      s='G'
    else:
      s='T'
    while(s==c):
      temp = randrange(4)
      if temp == 0:
        s='A'
      elif temp == 1:
        s='C'
      elif temp == 2:
        s='G'
      else:
        s='T'
    return centroid[0:p]+s+centroid[p+1:]

def drawOrigin(strandLength=20):
    strand =''
    for i in range(0,strandLength):
      temp = randrange(4)
      if temp == 0:
        strand+='A'
      elif temp == 1:
        strand+='C'
      elif temp == 2:
        strand+='G'
      else:
        strand+='T'
    return strand
    
# start by reading the command line
numClusters, \
numstrands, \
output, \
strandLength = handleArgs(sys.argv)

writer = open(output,'w')

# step 1: generate each DNA centroid
centroids_radii = []
minDistance = 5
for i in range(0, numClusters):
    centroid = drawOrigin(strandLength)
    # is it far enough from the others?
    while (tooClose(centroid, centroids_radii, minDistance)):
        centroid = drawOrigin(strandLength)
    centroids_radii.append(centroid)

# step 2: generate the strands for each centroid
strands = []
for i in range(0, numClusters):
    # compute the variance for this cluster
    cluster = centroids_radii[i]
    for j in range(0, numstrands):
        # generate a distance
        distance = randrange(1,minDistance)
      
        # generate a DNA strand that is at the distance from centroid
        strand = drawDNAatDistance(distance,cluster)
        # write the strands out
        
        writer.write(strand+'\n')
