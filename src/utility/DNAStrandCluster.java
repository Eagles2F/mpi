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
    
    
    //recalculate the new centroid
    public void calculateCentroid(){
    	int a = 0;
    	int g = 0;
    	int c = 0;
    	int t = 0;
    	DNAStrand dna = new DNAStrand("");
    	for(int i =0;i< this.centroid.getStrand().length();i++){
    		//calculate the mode character in each position
    		for(int j=0;j< this.getCluster().size();j++){
    			switch(this.cluster.get(j).getStrand().charAt(i)){
    			case 'A':
    				a++;
    				break;
    			case 'G':
    				g++;
    				break;
    			case 'C':
    				c++;
    				break;
    			case 'T':
    				t++;
    				break;
    			default:
    				break;
    			}
    		}
    		ArrayList<Integer> agct = new ArrayList<Integer>();
    		ArrayList<String> s = new ArrayList<String>();
    		agct.add(a);
    		s.add("A");
    		agct.add(g);
    		s.add("G");
    		agct.add(c);
    		s.add("C");
    		agct.add(t);
    		s.add("T");
    		int max=a;
    		int maxid=0;
    		for(int j=0;j<4;j++){
    			if(agct.get(j)>max){
    				max = agct.get(j);
    				maxid =j;
    			}
    		}
    		dna.setStrand(dna.getStrand()+s.get(maxid));
    	}
    	this.setCentroid(dna);
    }
    
	public DNAStrand getCentroid() {
		return centroid;
	}
	public void setCentroid(DNAStrand centroid) {
		this.centroid = centroid;
	}
	
	public void add(DNAStrand pt){
        cluster.add(pt);
    }
    public void addCluster(DNAStrandCluster newCluster){
        cluster.addAll(newCluster.getCluster());
    }
	public void clearCluster(){
		this.cluster.clear();
	}
}
