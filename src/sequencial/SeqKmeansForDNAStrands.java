/*
 * This is the sequencial Kmeans clustering class for the DNA Strands data set
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

import utility.DNAStrandDataLoader;
import utility.DNAStrandCluster;
import utility.DNAStrand;

public class SeqKmeansForDNAStrands {
	private ArrayList<DNAStrand> rawData;
	private ArrayList<DNAStrandCluster> clusters;
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

    private int miu=100; //iterating rounds
	
	public SeqKmeansForDNAStrands(){
		this.rawData = new ArrayList<DNAStrand>();
		this.clusters = new ArrayList<DNAStrandCluster>();
	}
	
	//the kmeans clustering method
	public void KmeansCluster(int k){
		//step 1: initialize centroids and clusters
		for(int i =0; i<k;i++){
			DNAStrandCluster cluster = new DNAStrandCluster();
			cluster.setCentroid(rawData.get(i));
			this.clusters.add(cluster);
		}
		
		for(int i=0; i<miu;i++){
			// clear each cluster 
			for(int n=0;n<k;n++){
				this.clusters.get(n).clearCluster();
			}
			//step 2: assign each data point to a cluster which is closer to it.
			for(int j=0;j<this.rawData.size();j++){
				DNAStrand p = rawData.get(j);
				
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
	
	public static void main(String[] args){
		//Input&Output File
		String input = "../input/DNA.txt";
		String output = "../output/DNAResult.txt";
		//number of clusters
		int k=2;
		
		
		SeqKmeansForDNAStrands DNACase = new SeqKmeansForDNAStrands();
		//load the data
		DNAStrandDataLoader loader = new DNAStrandDataLoader(input);
		DNACase.rawData = loader.loadData();
		
		//running the Kmeans clustering
		DNACase.KmeansCluster(k);
		
		//output the result
		DNACase.outputResults(output);
	}
}
