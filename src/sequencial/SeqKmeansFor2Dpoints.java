/*
 * This is the sequencial Kmeans clustering class for the 2D points data set
 * 
 * @Author: Yifan Li
 * @Date: 12/1/2014
 */

package sequencial;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import utility.TwoDCluster;
import utility.TwoDPoint;
import utility.TwoDpointsDataLoader;

public class SeqKmeansFor2Dpoints {
	private ArrayList<TwoDPoint> rawData;
	private ArrayList<TwoDCluster> clusters;
	private int miu=100; //iterating rounds
	
	public SeqKmeansFor2Dpoints(){
		this.setRawData(new ArrayList<TwoDPoint>());
		this.clusters = new ArrayList<TwoDCluster>();
	}
	
	//the kmeans clustering method
	public void KmeansCluster(int k){
		//step 1: initialize centroids and clusters
		for(int i =0; i<k;i++){
			TwoDCluster cluster = new TwoDCluster();
			cluster.setCentroid(getRawData().get(i));
			this.clusters.add(cluster);
		}
		
		for(int i=0; i<miu;i++){
			// clear each cluster 
			for(int n=0;n<k;n++){
				this.clusters.get(n).clearCluster();
			}
			//step 2: assign each data point to a cluster which is closer to it.
			for(int j=0;j<this.getRawData().size();j++){
				TwoDPoint p = getRawData().get(j);
				
				//calculate which cluster is closer to the data point
				int idCluster = 0;
				double disMin = 9999999;//infinite
				double disCurrent = 0;
				for(int n=0;n<k;n++){
					 disCurrent = p.distance(this.clusters.get(n).getCentroid());
					 if(disCurrent<disMin){ 
						 idCluster = n;
						 disMin = disCurrent;
					 }
				}
				
				//assign the data point into the cluster
				this.clusters.get(idCluster).add(p);
			}
			
			//step 3: recalculate the centroids in each cluster
			for(int n=0;n<k;n++){
				this.clusters.get(n).calculateCentroid();
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
	
	public static void main(String[] args){
		//Input&Output File
		String input = "../input/cluster.csv";
		String output = "../output/twoDResult.txt";
		//number of clusters
		int k=2;
		
		
		SeqKmeansFor2Dpoints TwoDCase = new SeqKmeansFor2Dpoints();
		//load the data
		TwoDpointsDataLoader loader = new TwoDpointsDataLoader(input);
		TwoDCase.setRawData(loader.loadData());
		
		//running the Kmeans clustering
		TwoDCase.KmeansCluster(k);
		
		//output the result
		TwoDCase.outputResults(output);
	}

    public ArrayList<TwoDPoint> getRawData() {
        return rawData;
    }

    public void setRawData(ArrayList<TwoDPoint> rawData) {
        this.rawData = rawData;
    }
}
