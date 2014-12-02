/*
 * This is the sequencial Kmeans clustering class for the 2D points data set
 * 
 * @Author: Yifan Li
 * @Date: 12/1/2014
 */

package sequencial;

import java.util.ArrayList;

import utility.TwoDCluster;
import utility.TwoDPoint;
import utility.TwoDpointsDataLoader;

public class SeqKmeansFor2Dpoints {
	private ArrayList<TwoDPoint> rawData;
	private ArrayList<TwoDPoint> centroids;
	private ArrayList<TwoDCluster> clusters;
	private int miu; //iterating rounds
	
	//the kmeans clustering method
	public void KmeansCluster(int k){
		//step 1: initialize centroids
		for(int i =0; i<k;i++){
			centroids.add(new TwoDPoint(rawData.get(i)));
		}
		for(int i=0; i<miu;i++){
		//step 2: assign each data point to a cluster
			
			
		//step 3: recaculate the centroids in each cluster
			
		}
	}
	
	//output the clustering results
	public void outputResults(String outputFile){
		
	}
	
	static void main(){
		//Input File
		String input = "cluster.csv";
		//number of clusters
		int k=2;
		
		
		SeqKmeansFor2Dpoints TwoDCase = new SeqKmeansFor2Dpoints();
		//load the data
		TwoDpointsDataLoader loader = new TwoDpointsDataLoader(input);
		TwoDCase.rawData = loader.loadData();
		
		//running the Kmeans clustering
		TwoDCase.KmeansCluster(k);
	}
}
