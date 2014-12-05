package parallel;
import mpi.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import sequencial.SeqKmeansFor2Dpoints;
import utility.DNAStrand;
import utility.DNAStrandCluster;
import utility.DNAStrandDataLoader;
import utility.MPIMessage;
import utility.DNAStrandCluster;
import utility.TwoDPoint;
import utility.TwoDpointsDataLoader;

public class ParallelDNA{
    private ArrayList<DNAStrand> rawData;
    private ArrayList<DNAStrandCluster> clusters;
    public int rank;
    

    private int miu; //iterating rounds
    private int k;
    
    public int getK() {
        return k;
    }
    public void setK(int k) {
        this.k = k;
    }
    public int getMiu() {
        return miu;
    }
    public void setMiu(int miu) {
        this.miu = miu;
    }
    public ParallelDNA(int k){
        this.rawData = new ArrayList<DNAStrand>();
        this.clusters = new ArrayList<DNAStrandCluster>();
        this.rawData = null;
        this.k = k;
        this.miu = 10;
        for(int i =0; i<k;i++){
            DNAStrandCluster cluster = new DNAStrandCluster();
            this.clusters.add(cluster);
        }
    }
    public ArrayList<DNAStrand> getRawData() {
        return rawData;
    }

    public void setRawData(ArrayList<DNAStrand> rawData) {
        this.rawData = rawData;
    }

    public ArrayList<DNAStrandCluster> getClusters() {
        return clusters;
    }

    public void setClusters(ArrayList<DNAStrandCluster> clusters) {
        this.clusters = clusters;
    }
    public static void main(String[] args){
        int size;
    try {
        System.out.println("start Init");
        ParallelDNA pDNA = new ParallelDNA(2);
        MPI.Init(args);
        System.out.println("get rank");
        pDNA.rank = MPI.COMM_WORLD.Rank();
        System.out.println("get size");
        size = MPI.COMM_WORLD.Size() - 1;
        
        if(size < 2) {
            System.out.println("Please configur more than 2 processes.");
            return;
        }
        if(pDNA.rank == 0) {
            String input = "../input/DNA.txt";
            String output = "../output/DNAResult.txt";
            //number of clusters
            
            
            
            //load the data
            DNAStrandDataLoader loader = new DNAStrandDataLoader(input);
            pDNA.setRawData(loader.loadData());
            long start = System.currentTimeMillis();
            pDNA.assignTasks(pDNA.getRawData(),size,pDNA.k);
            pDNA.repeat(size, pDNA.k);
            long end = System.currentTimeMillis();
            long duration = end - start;
            System.out.println("duration: "+duration);
            pDNA.outputResults(output);
            
        }else{
           while(true){
               Object[] messageArray = new Object[2];
               try{
                   MPI.COMM_WORLD.Recv(messageArray, 0, 2, MPI.OBJECT, 0, 0);
               }catch(MPIException e) {
                              
                   e.printStackTrace();
               } catch(Exception e) {
                                                    
                   e.printStackTrace();
               }
               
               MPIMessage msg = (MPIMessage)messageArray[0];
               System.out.println("Message received: " + msg.getCmdId());
               if(msg.getCmdId() == MPIMessage.CommandId.CLUSTER){
                   //only transmit the rawData once
                   if(pDNA.getRawData() == null){
                       pDNA.setRawData(msg.getDNARawData());
                   }
                 //step 2: assign each data point to a cluster which is closer to it.
                 //clear the cluster every time
                for(int i=0;i<pDNA.k;i++){
                     pDNA.clusters.get(i).clearCluster();
                }
                   for(int j=0;j<pDNA.getRawData().size();j++){
                       DNAStrand p = pDNA.getRawData().get(j);
                       
                       //calculate which cluster is closer to the data point
                       int idCluster = 0;
                       double disMin = 9999999;//infinite
                       double disCurrent = 0;
                       for(int n=0;n<pDNA.k;n++){
                            disCurrent = p.distance(msg.getDNACentroid().get(n));
                            if(disCurrent<disMin){ 
                                idCluster = n;
                                disMin = disCurrent;
                            }
                       }
                       

                       //assign the data point into the cluster
                       pDNA.clusters.get(idCluster).add(p);
                   }
                   for(int m=0;m<pDNA.k;m++){
                       pDNA.clusters.get(m).setCentroid(msg.getDNACentroid().get(m));
                   }
                   msg.setRspId(MPIMessage.ResponseId.CLUSTERRSP);
                   msg.setDNAClusters(pDNA.clusters);
                   messageArray[0] = msg;
                   messageArray[1] = null;
                   try{
                       MPI.COMM_WORLD.Send(messageArray, 0, 2, MPI.OBJECT, 0, 0);
                   }catch(MPIException e) {
                                  
                       e.printStackTrace();
                   } catch(Exception e) {
                                                        
                       e.printStackTrace();
                   }
               }
           }
        }
        System.out.println("finalize");
        MPI.Finalize();
        
       }catch(MPIException e) {
           
           e.printStackTrace();
       } catch(Exception e) {
           
           e.printStackTrace();
       }
}

    private void assignTasks(ArrayList<DNAStrand> rawData2, int size, int k) {
      //step 1: initialize centroids and clusters
        for(int j=0;j<size;j++){
            MPIMessage msg = new MPIMessage();
            msg.setCmdId(MPIMessage.CommandId.CLUSTER);
            for(int i =0; i<k;i++){
                
                msg.addDNACentroid(rawData2.get(i));
                
            }
            ArrayList<DNAStrand> rawDataSend = new ArrayList<DNAStrand>();
            int chunk = rawData2.size()/size;
            for(int m=j*chunk;m<(j+1)*chunk;m++){
                rawDataSend.add(rawData2.get(m));
            }
            //for the last chunk, need append the remainder of the rawData
            if(j == (size-1)){
            
                for(int l=(j+1)*chunk;l<rawData2.size();l++){
                rawDataSend.add(rawData2.get(l));
                }
            }
            msg.setDNARawData(rawDataSend);
            Object[] MPIMsgArray = new Object[2];
            MPIMsgArray[0] = msg;
            MPIMsgArray[1] = null;
            System.out.println(j+" node 0 send to node " + j+1 + ", message " + msg.getCmdId());
            try{
                MPI.COMM_WORLD.Send(MPIMsgArray, 0, 2, MPI.OBJECT, j+1, 0);
            }catch(MPIException e) {
                           
                e.printStackTrace();
            } catch(Exception e) {
                                                 
                e.printStackTrace();
            }
        }
        
    }
    
    private void repeat(int size, int k){
        for(int i=0;i<getMiu();i++){
            for(int j=0;j<size;j++){
                Object[] MPIMsgArray = new Object[2];
                try{
                    MPI.COMM_WORLD.Recv(MPIMsgArray, 0, 2, MPI.OBJECT, j+1, 0);
                }catch(MPIException e) {
                               
                    e.printStackTrace();
                } catch(Exception e) {
                                                     
                    e.printStackTrace();
                }
                MPIMessage msg = (MPIMessage)MPIMsgArray[0];
                //conbine the subCluster from every node
                for(int n=0;n<k;n++){
                    if(getClusters().size() < k){
                        getClusters().add(msg.getDNAClusters().get(n));
                        getClusters().get(n).setCentroid(msg.getDNAClusters().get(n).getCentroid());
                    }else{
                        getClusters().get(n).addCluster(msg.getDNAClusters().get(n));
                }
                
            }
            }
            
            //step 3: recalculate the centroids in each cluster
            for(int n=0;n<k;n++){
                clusters.get(n).calculateCentroid();
                
            }
            if(i == (getMiu()-1)){
                return;
            }
            for(int j=0;j<size;j++){
                Object[] MPIMsgArray = new Object[2];
                MPIMessage msg = new MPIMessage();
                msg.setCmdId(MPIMessage.CommandId.CLUSTER);
                for(int q =0; q<k;q++){
                    
                    msg.addDNACentroid(clusters.get(q).getCentroid());
                    getClusters().get(q).clearCluster();
                }
                MPIMsgArray[0] = msg;
                MPIMsgArray[1] = null;
                System.out.println(j+" node 0 send to node " + j+1 + ", message " + msg.getCmdId());
                try{
                    MPI.COMM_WORLD.Send(MPIMsgArray, 0, 2, MPI.OBJECT, j+1, 0);
                }catch(MPIException e) {
                               
                    e.printStackTrace();
                } catch(Exception e) {
                                                     
                    e.printStackTrace();
                }
            }
            
        }
    }

    
  //output the clustering results
    public void outputResults(String outputFile){
        File file = new File(outputFile);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        try(BufferedWriter bw= new BufferedWriter(new FileWriter(outputFile))){
            for(int i=0;i<this.clusters.size();i++){
                bw.write("Cluster "+i+"\n");
                bw.write(" Centroid: "+this.clusters.get(i).getCentroid().getStrand()+"\n");
                for(int j=0;j<this.clusters.get(i).getCluster().size();j++){
                    bw.write("     "+this.clusters.get(i).getCluster().get(j).getStrand()
                            +" distance to the Centroid:"+
                            this.clusters.get(i).getCluster().get(j).distance(this.clusters.get(i).getCentroid())
                            +"\n");
                }
            }
        } catch (IOException e) {
            
            e.printStackTrace();
        } 
    }
}
