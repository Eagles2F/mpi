package parallel;
import mpi.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import sequencial.SeqKmeansFor2Dpoints;
import utility.MPIMessage;
import utility.TwoDCluster;
import utility.TwoDPoint;
import utility.TwoDpointsDataLoader;

public class ParallelTwoD{
    private ArrayList<TwoDPoint> rawData;
    private ArrayList<TwoDCluster> clusters;
    private ArrayList<Long> runningTime; //every process has a running time
    

    private int miu; //iterating rounds
    private int k;
    public int rank;
    private int size; //process size
    
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
    public ParallelTwoD(int k,int size){
        this.rawData = new ArrayList<TwoDPoint>();
        this.clusters = new ArrayList<TwoDCluster>();
        this.runningTime = new ArrayList<Long>();
        this.rawData = null;
        this.k = k;
        this.miu = 10;
        for(int i =0; i<k;i++){
            TwoDCluster cluster = new TwoDCluster();
            this.clusters.add(cluster);
            
        }
        for(int i=0;i<size;i++){
            Long zero = new Long(0);
            runningTime.add(zero);
        }
    }
    public ArrayList<TwoDPoint> getRawData() {
        return rawData;
    }

    public void setRawData(ArrayList<TwoDPoint> rawData) {
        this.rawData = rawData;
    }

    public ArrayList<TwoDCluster> getClusters() {
        return clusters;
    }

    public void setClusters(ArrayList<TwoDCluster> clusters) {
        this.clusters = clusters;
    }
    public static void main(String[] args){
        int size;
        
    try {
        System.out.println("start Init");
        
        MPI.Init(args);
        System.out.println("get rank");
        
        System.out.println("get size");
        size = MPI.COMM_WORLD.Size() - 1;
        ParallelTwoD pTwoD = new ParallelTwoD(Integer.valueOf(args[0]),size);
        pTwoD.setMiu(Integer.valueOf(args[1]));
        pTwoD.rank = MPI.COMM_WORLD.Rank();
        if(size < 2) {
            System.out.println("Please configur more than 2 processes.");
            return;
        }
        if(pTwoD.rank == 0) {
            String input = "../input/cluster.csv";
            String output = "../output/twoDResult.txt";
            //number of clusters
            
            
            
            //load the data
            TwoDpointsDataLoader loader = new TwoDpointsDataLoader(input);
            pTwoD.setRawData(loader.loadData());
             
            long start = System.currentTimeMillis();
            pTwoD.assignTasks(pTwoD.getRawData(),size,pTwoD.k);
            pTwoD.repeat(size, pTwoD.k);
            long end = System.currentTimeMillis();
            long duration = end - start;
            System.out.println("duration: "+duration);
            System.out.println("runtime:");
            for(int i=0;i<pTwoD.getRunningTime().size();i++){
                System.out.println("proc "+(i+1)+": "+pTwoD.getRunningTime().get(i));
            }
            pTwoD.outputResults(output);
            
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
               
               long start = System.currentTimeMillis();
               MPIMessage msg = (MPIMessage)messageArray[0];
               
               if(msg.getCmdId() == MPIMessage.CommandId.CLUSTER){
                   int lastRun = msg.getLastRun();
                   //only transmit the rawData once
                   if(pTwoD.getRawData() == null){
                       pTwoD.setRawData(msg.getRawData());
                   }
                 //step 2: assign each data point to a cluster which is closer to it.
                   
                   //clear the cluster every time
                   for(int i=0;i<pTwoD.k;i++){
                        pTwoD.clusters.get(i).clearCluster();
                   }
                   for(int j=0;j<pTwoD.getRawData().size();j++){
                       TwoDPoint p = pTwoD.getRawData().get(j);
                       
                       //calculate which cluster is closer to the data point
                       int idCluster = 0;
                       double disMin = 9999999;//infinite
                       double disCurrent = 0;
                       for(int n=0;n<pTwoD.k;n++){
                            disCurrent = p.distance(msg.getCentroid().get(n));
                            if(disCurrent<disMin){ 
                                idCluster = n;
                                disMin = disCurrent;
                            }
                       }
                       

                       //assign the data point into the cluster
                       //System.out.println("rank "+pTwoD.rank+"x: "+p.getX()+"y: "+p.getY());
                       pTwoD.clusters.get(idCluster).add(p);
                   }
                   
                   for(int m=0;m<pTwoD.k;m++){
                       pTwoD.clusters.get(m).calculateCentroid();
                       msg.addCentroid(pTwoD.clusters.get(m).getCentroid());
                       msg.addPointNumber(pTwoD.clusters.get(m).getCluster().size());
                   }
                   
                   long end = System.currentTimeMillis();
                   long duration = end - start;
                   msg.setRspId(MPIMessage.ResponseId.CLUSTERRSP);
                   if(lastRun == 1){
                       msg.setClusters(pTwoD.clusters);
                   }
                   
                   msg.setRunningTime(duration);
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

    public ArrayList<Long> getRunningTime() {
        return runningTime;
    }
    public void setRunningTime(ArrayList<Long> runningTime) {
        this.runningTime = runningTime;
    }
    private void assignTasks(ArrayList<TwoDPoint> rawData2, int size, int k) {
      //step 1: initialize centroids and clusters
        for(int j=0;j<size;j++){
            MPIMessage msg = new MPIMessage();
            msg.setCmdId(MPIMessage.CommandId.CLUSTER);
            for(int i =0; i<k;i++){
                
                msg.addCentroid(rawData2.get(i));
                
            }
            ArrayList<TwoDPoint> rawDataSend = new ArrayList<TwoDPoint>();
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
            msg.setRawData(rawDataSend);
            Object[] MPIMsgArray = new Object[2];
            MPIMsgArray[0] = msg;
            MPIMsgArray[1] = null;
            //System.out.println(j+" node 0 send to node " + j+1 + ", message " + msg.getCmdId());
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
            MPIMessage msg = null;
            for(int j=0;j<size;j++){
                Object[] MPIMsgArray = new Object[2];
                try{
                    MPI.COMM_WORLD.Recv(MPIMsgArray, 0, 2, MPI.OBJECT, j+1, 0);
                }catch(MPIException e) {
                               
                    e.printStackTrace();
                } catch(Exception e) {
                                                     
                    e.printStackTrace();
                }
                msg = (MPIMessage)MPIMsgArray[0];
                //calculate new centroid
                for(int n=0;n<k;n++){
                    double x = clusters.get(n).getCentroid().getX()+msg.getCentroid().get(n).getX()*msg.getPointNumber().get(n);
                    double y = clusters.get(n).getCentroid().getY()+msg.getCentroid().get(n).getY()*msg.getPointNumber().get(n);
                    clusters.get(n).getCentroid().setX(x);
                    clusters.get(n).getCentroid().setY(y);
                    
                }
                long time = runningTime.get(j) + msg.getRunningTime();
                //System.out.println("new run time proc "+(j+1)+" "+time);
                runningTime.set(j, time); 
                
            }
            for(int j=0;j<k;j++){
                TwoDPoint centroid = new TwoDPoint();
                centroid.setX(clusters.get(j).getCentroid().getX()/rawData.size());
                centroid.setY(clusters.get(j).getCentroid().getY()/rawData.size());
                clusters.get(j).setCentroid(centroid);
            }
                
            
            
            //step 3: recalculate the centroids in each cluster
            
            if(i == (getMiu()-1)){
                for(int n=0;n<k;n++){
                    //System.out.println("receive sub cluster "+n+"size: "+msg.getClusters().get(n).getCluster().size());
                    
                        getClusters().add(msg.getClusters().get(n));
                        
                    
                }
                return;
            }
            for(int j=0;j<size;j++){
                Object[] MPIMsgArray = new Object[2];
                MPIMessage msgSend = new MPIMessage();
                msgSend.setCmdId(MPIMessage.CommandId.CLUSTER);
                for(int q =0; q<k;q++){
                    
                    msgSend.addCentroid(clusters.get(q).getCentroid());
                    getClusters().get(q).clearCluster();
                }
                MPIMsgArray[0] = msgSend;
                MPIMsgArray[1] = null;
                //System.out.println(j+" node 0 send to node " + j+1 + ", message " + msg.getCmdId());
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
                bw.write(" Centroid: "+this.clusters.get(i).getCentroid().getX()
                        +","+this.clusters.get(i).getCentroid().getY()+"\n");
                for(int j=0;j<this.clusters.get(i).getCluster().size();j++){
                    bw.write("     "+this.clusters.get(i).getCluster().get(j).getX()+
                            ","+this.clusters.get(i).getCluster().get(j).getY()
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
