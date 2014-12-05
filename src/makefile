JC = mpijavac
.SUFFIXES: .java .class
.java.class:
	$(JC) $*.java

CLASSES = \
	sequencial/SeqKmeansFor2Dpoints.java\
	sequencial/SeqKmeansForDNAStrands.java\
	utility/DNAStrand.java\
	utility/MPIMessage.java\
	utility/TwoDpointsDataLoader.java\
	utility/DNAStrandCluster.java\
	utility/TwoDCluster.java\
	utility/DNAStrandDataLoader.java\
	utility/TwoDPoint.java\
	parallel/ParallelTwoD.java\
	parallel/ParallelDNA.java


default: classes

classes: 
	$(JC) ./sequencial/SeqKmeansFor2Dpoints.java -d ../bin/
	$(JC) ./sequencial/SeqKmeansForDNAStrands.java -d ../bin/
	$(JC) ./utility/DNAStrand.java -d ../bin/
	$(JC) ./utility/MPIMessage.java -d ../bin/
	$(JC) ./utility/TwoDpointsDataLoader.java -d ../bin/
	$(JC) ./utility/DNAStrandCluster.java -d ../bin/
	$(JC) ./utility/TwoDCluster.java -d ../bin/
	$(JC) ./utility/DNAStrandDataLoader.java -d ../bin/
	$(JC) ./utility/TwoDPoint.java -d ../bin/
	$(JC) ./parallel/ParallelTwoD.java -d ../bin/
	$(JC) ./parallel/ParallelDNA.java -d ../bin/
	

clean:
	$(RM) -rf ../bin/*
