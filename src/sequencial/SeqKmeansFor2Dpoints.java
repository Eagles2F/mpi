/*
 * This is the sequencial Kmeans clustering class for the 2D points data set
 * 
 * @Author: Yifan Li
 * @Date: 12/1/2014
 */

package sequencial;

import java.util.ArrayList;

import utility.TwoDPoint;
import utility.TwoDpointsDataLoader;

public class SeqKmeansFor2Dpoints {
	private ArrayList<TwoDPoint> rawData;
	private ArrayList<TwoDPoint> centroids;
	
	//the kmeans clustering method
	public KmeansCluster(int k){
		
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
		
	}
}
