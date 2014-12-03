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
    public ParallelTwoD(){
        this.rawData = new ArrayList<TwoDPoint>();
        this.clusters = new ArrayList<TwoDCluster>();
        this.rawData = null;
        this.k = 2;
        this.miu = 100;
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
        int rank;
        int size;
    try {
        MPI.Init(args);
        rank = MPI.COMM_WORLD.Rank();
        size = MPI.COMM_WORLD.Size() - 1;
        
        if(size < 2) {
            System.out.println("Please configur more than 2 processes.");
            return;
        }
        ParallelTwoD pTwoD = new ParallelTwoD();
        if(rank == 0) {
            String input = "../input/cluster.csv";
            String output = "../output/twoDResult.txt";
            //number of clusters
            
            
            
            SeqKmeansFor2Dpoints TwoDCase = new SeqKmeansFor2Dpoints();
            //load the data
            TwoDpointsDataLoader loader = new TwoDpointsDataLoader(input);
            TwoDCase.setRawData(loader.loadData());
            
            pTwoD.assignTasks(TwoDCase.getRawData(),size,pTwoD.k);
            pTwoD.repeat(size, pTwoD.k);
            pTwoD.outputResults(output);
            
        }else{
           while(true){
               Object[] messageArray = new Object[2];
               MPI.COMM_WORLD.Recv(messageArray, 0, 2, MPI.OBJECT, 0, 0);
               
               MPIMessage msg = (MPIMessage)messageArray[0];
               System.out.println("Message received: " + msg.getCmdId());
               if(msg.getCmdId() == MPIMessage.CommandId.CLUSTER){
                   //only transmit the rawData once
                   if(pTwoD.getRawData() == null){
                       pTwoD.setRawData(msg.getRawData());
                   }
                 //step 2: assign each data point to a cluster which is closer to it.
                   for(int j=0;j<pTwoD.getRawData().size();j++){
                       TwoDPoint p = pTwoD.getRawData().get(j);
                       
                       //calculate which cluster is closer to the data point
                       int idCluster = 0;
                       double disMin = 9999999;//infinite
                       double disCurrent = 0;
                       for(int n=0;n<pTwoD.k;n++){
                            disCurrent = p.distance(pTwoD.clusters.get(n).getCentroid());
                            if(disCurrent<disMin){ 
                                idCluster = n;
                                disMin = disCurrent;
                            }
                       }
                       
                       //assign the data point into the cluster
                       pTwoD.clusters.get(idCluster).add(p);
                   }
                   msg.setRspId(MPIMessage.ResponseId.CLUSTERRSP);
                   msg.setClusters(pTwoD.clusters);
                   messageArray[0] = msg;
                   messageArray[1] = null;
                   MPI.COMM_WORLD.Send(messageArray, 0, 2, MPI.OBJECT, 0, 0);
               }
           }
        }
        MPI.Finalize();
        
       }catch(MPIException e) {
           
           e.printStackTrace();
       } catch(Exception e) {
           
           e.printStackTrace();
       }
}

    private void assignTasks(ArrayList<TwoDPoint> rawData2, int size, int k) {
      //step 1: initialize centroids and clusters
        for(int j=0;j<size;j++){
            MPIMessage msg = new MPIMessage();
            msg.setCmdId(MPIMessage.CommandId.CLUSTER);
            for(int i =0; i<k;i++){
                
                msg.setCentroid(this.getRawData().get(i));
                
            }
            ArrayList<TwoDPoint> rawDataSend = new ArrayList<TwoDPoint>();
            int chunk = this.getRawData().size()/size;
            for(int m=j*chunk;m<(j+1)*chunk;m++){
                rawDataSend.add(this.rawData.get(m));
            }
            msg.setRawData(rawDataSend);
            Object[] MPIMsgArray = new Object[2];
            MPIMsgArray[0] = msg;
            MPIMsgArray[1] = null;
            System.out.println("node 0 send to node " + j + ", message " + msg.getCmdId());
            MPI.COMM_WORLD.Send(MPIMsgArray, 0, 2, MPI.OBJECT, j, 0);
        }
        
    }
    
    private void repeat(int size, int k){
        for(int i=0;i<getMiu();i++){
            for(int j=0;j<size;j++){
                Object[] MPIMsgArray = new Object[2];
                MPI.COMM_WORLD.Recv(MPIMsgArray, 0, 2, MPI.OBJECT, j, 0);
                MPIMessage msg = (MPIMessage)MPIMsgArray[0];
                //conbine the subCluster from every node
                for(int n=0;n<k;n++){
                    getClusters().get(n).addCluster(msg.getClusters().get(n));
                }
                
            }
            
            //step 3: recalculate the centroids in each cluster
            for(int n=0;n<k;n++){
                clusters.get(n).calculateCentroid();
                
            }
            for(int j=0;j<size;j++){
                Object[] MPIMsgArray = new Object[2];
                MPIMessage msg = new MPIMessage();
                msg.setCmdId(MPIMessage.CommandId.CLUSTER);
                for(int q =0; q<k;q++){
                    
                    msg.setCentroid(clusters.get(q).getCentroid());
                    
                }
                MPIMsgArray[0] = msg;
                MPIMsgArray[1] = null;
                System.out.println("node 0 send to node " + j + ", message " + msg.getCmdId());
                MPI.COMM_WORLD.Send(MPIMsgArray, 0, 2, MPI.OBJECT, j, 0);
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
                            ","+this.clusters.get(i).getCluster().get(j).getX()
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
