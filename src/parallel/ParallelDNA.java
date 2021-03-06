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

public class ParallelDNA{
    private ArrayList<DNAStrand> rawData;
    private ArrayList<DNAStrandCluster> clusters;
    private ArrayList<Long> runningTime; //every process has a running time
    public ArrayList<Long> getRunningTime() {
        return runningTime;
    }
    public void setRunningTime(ArrayList<Long> runningTime) {
        this.runningTime = runningTime;
    }


    public int rank;
    

    private int miu; //iterating rounds
    private int k;
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
    public ParallelDNA(int k,int size){
        this.rawData = new ArrayList<DNAStrand>();
        this.clusters = new ArrayList<DNAStrandCluster>();
        this.runningTime = new ArrayList<Long>();
        this.rawData = null;
        this.k = k;
        
        for(int i =0; i<k;i++){
            DNAStrandCluster cluster = new DNAStrandCluster();
            DNAStrand centroid = new DNAStrand(null);
            cluster.setCentroid(centroid);
            cluster.setNumber(0);
            this.clusters.add(cluster);
        }
        for(int i=0;i<size;i++){
            Long zero = new Long(0);
            runningTime.add(zero);
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
       if(args.length != 2){
            System.out.println("Usage:java parallel.ParallelTwoD  <number of clusters> <number of iterations>");
        }
       try {
        System.out.println("start Init");
         
        MPI.Init(args);
        
        size = MPI.COMM_WORLD.Size() - 1;
        ParallelDNA pDNA = new ParallelDNA(Integer.valueOf(args[0]),size);
        pDNA.setMiu(Integer.valueOf(args[1]));
        pDNA.rank = MPI.COMM_WORLD.Rank();
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
            System.out.println("system runtime: "+duration);
            System.out.println("every process runtime:");
            long total = 0;
            for(int i=0;i<pDNA.getRunningTime().size();i++){
            System.out.println("proc "+(i+1)+": "+pDNA.getRunningTime()    .get(i));
            total += pDNA.getRunningTime().get(i);
            }
            System.out.println("total process runtime: "+total);
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
               long start = System.currentTimeMillis();
               MPIMessage msg = (MPIMessage)messageArray[0];
               int lastRun = msg.getLastRun();
               MPIMessage msgSend = new MPIMessage();
               
               if(msg.getCmdId() == MPIMessage.CommandId.CLUSTER){
                   //only transmit the rawData once
                   if(pDNA.getRawData() == null){
                       pDNA.setRawData(msg.getDNARawData());
                   }
                 //step 2: assign each data point to a cluster which is closer to it.
                 //clear the cluster every time
                for(int i=0;i<pDNA.k;i++){
                     pDNA.clusters.get(i).clearCluster();
                     pDNA.clusters.get(i).setCentroid(msg.getDNACentroid().get(i));
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
                       pDNA.clusters.get(m).calculateCentroid();
                       msgSend.addDNACentroid(pDNA.clusters.get(m).getCentroid());
                       msgSend.addDNANumber(pDNA.clusters.get(m).getCluster().size());
                   }
                   long end = System.currentTimeMillis();
                   long duration = end - start;
                   msgSend.setRspId(MPIMessage.ResponseId.CLUSTERRSP);
                   if(lastRun == 1){
                       msgSend.setDNAClusters(pDNA.clusters);
                   }
                   msgSend.setRunningTime(duration);
                   messageArray[0] = msgSend;
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
                //conbine the subCluster from every node
                for(int n=0;n<k;n++){
                    //accumulate number
                    this.clusters.get(n).setNumber(this.clusters.get(n).getNumber()+msg.getDNANumber().get(n));
                    //accumulate counter
                    for(int m=0;m<msg.getDNACentroid().get(n).getStrand().length();m++){
                        switch(msg.getDNACentroid().get(n).getStrand().charAt(m)){
                        case 'A':
                            this.clusters.get(n).a.set(m, this.clusters.get(n).a.get(m)+msg.getDNANumber().get(n));
                            break;
                        case 'G':
                            this.clusters.get(n).g.set(m, this.clusters.get(n).g.get(m)+msg.getDNANumber().get(n));
                            break;
                        case 'C':
                            this.clusters.get(n).c.set(m, this.clusters.get(n).c.get(m)+msg.getDNANumber().get(n));
                            break;
                        case 'T':
                            this.clusters.get(n).t.set(m, this.clusters.get(n).t.get(m)+msg.getDNANumber().get(n));
                            break;
                        default:
                            break;
                        }
                    }
        
            }
                long time = runningTime.get(j) + msg.getRunningTime();
                
                runningTime.set(j, time);
                if(i == (getMiu()-1)){
                    for(int n=0;n<k;n++){
                    getClusters().get(n).addCluster(msg.getDNAClusters().get(n));
                    }
                } 
           }
                if(i == (getMiu()-1)){
                    for(int n=0;n<k;n++){
                     getClusters().get(n).calculateCentroid();
                }
                    //this is the last run, we need to caculate the centroid and exit the loop
                    return;
            }
            
            //step 3: recalculate the centroids in each cluster
            for(int n=0;n<k;n++){
                ArrayList<String> s = new ArrayList<String>();
                s.add("A");
                s.add("G");
                s.add("C");
                s.add("T");
                DNAStrand dna = new DNAStrand("");
                //20 is the default length of DNA strand.
                for(int m =0;m< 20;m++){
                    int a = this.clusters.get(n).a.get(m);
                    int g = this.clusters.get(n).g.get(m);
                    int c = this.clusters.get(n).c.get(m);
                    int t = this.clusters.get(n).t.get(m);
                    
                    //calculate the mode character in each position
                    
                    ArrayList<Integer> agct = new ArrayList<Integer>();
                    
                    agct.add(a);
                    agct.add(g);
                    agct.add(c);    
                    agct.add(t);
                    int max=a;
                    int maxid=0;
                    for(int j=0;j<4;j++){
                        if(agct.get(j)>max){
                            max = agct.get(j);
                            maxid =j;
                        }
                    }
                    dna.setStrand(dna.getStrand()+s.get(maxid));
                    agct.clear();
                }
                
                clusters.get(n).setCentroid(dna);
                
            }
            
            for(int j=0;j<size;j++){
                Object[] MPIMsgArray = new Object[2];
                MPIMessage msgSend = new MPIMessage();
                msgSend.setCmdId(MPIMessage.CommandId.CLUSTER);
                //set last run, then worker could send the cluster back
                if(i == (getMiu()-2)){
                    msgSend.setLastRun(1);
                }
                for(int q =0; q<k;q++){
                    
                    msgSend.addDNACentroid(clusters.get(q).getCentroid());
                    getClusters().get(q).clearCluster();
                }
                MPIMsgArray[0] = msgSend;
                MPIMsgArray[1] = null;
                
                try{
                    MPI.COMM_WORLD.Send(MPIMsgArray, 0, 2, MPI.OBJECT, j+1, 0);
                }catch(MPIException e) {
                               
                    e.printStackTrace();
                } catch(Exception e) {
                                                     
                    e.printStackTrace();
                }
            }
          //clear the centroid
            for(int j=0;j<k;j++){
                DNAStrand centroid = new DNAStrand(null);
                clusters.get(j).setCentroid(centroid);
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
