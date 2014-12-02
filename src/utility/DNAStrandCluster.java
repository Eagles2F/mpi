package utility;

import java.io.Serializable;
import java.util.ArrayList;

public class DNAStrandCluster implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private DNAStrand centroid;
    
    private ArrayList<DNAStrand> cluster;
    public DNAStrandCluster(){
       cluster = new ArrayList<DNAStrand>();
       setCentroid(null);
    }
 
    public ArrayList<DNAStrand> getCluster() {
        return cluster;
    }
    public void setCluster(ArrayList<DNAStrand> cluster) {
        this.cluster = cluster;
    }
    public void add(DNAStrand pt){
        cluster.add(pt);
    }
    
    //recalculate the new centroid
    public void calculateCentroid(){
    	ArrayList<Integer> distance = new ArrayList<Integer>();
    	int temp=0;
    	for(DNAStrand dna:this.cluster){
    		temp =0;
    		for(DNAStrand dna1:this.cluster){
    			temp += dna1.distance(dna);
    		}
    		distance.add(temp);
    	}
    	
    	//the point which is closest to the other node becomes the new centroid
    	int mindistance = 99999;
    	int id = 0;
    	for(int i=0;i<distance.size();i++){
    		if(distance.get(i)<mindistance){
    			id = i;
    			mindistance = distance.get(i);
    		}
    	}
    	this.centroid = this.cluster.get(id);
    }
    
	public DNAStrand getCentroid() {
		return centroid;
	}
	public void setCentroid(DNAStrand centroid) {
		this.centroid = centroid;
	}
	
	public void clearCluster(){
		this.cluster.clear();
	}
}
