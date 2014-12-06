package utility;

import java.io.Serializable;
import java.util.ArrayList;

public class DNAStrandCluster implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private DNAStrand centroid;
    public ArrayList<Integer> a;
    public ArrayList<Integer> g;
    public ArrayList<Integer> c;
    public ArrayList<Integer> t;
    private int number;
    private ArrayList<DNAStrand> cluster;
    public DNAStrandCluster(){
       cluster = new ArrayList<DNAStrand>();
       setCentroid(new DNAStrand(""));
       a = new ArrayList<Integer>();
       g = new ArrayList<Integer>();
       c = new ArrayList<Integer>();
       t = new ArrayList<Integer>();
       for(int i=0; i<20;i++){
           a.add(0);
           g.add(0);
           c.add(0);
           t.add(0);
       }
       number =0;
    }
 
    public ArrayList<DNAStrand> getCluster() {
        return cluster;
    }
    public void setCluster(ArrayList<DNAStrand> cluster) {
        this.cluster = cluster;
    }
    
    
    //recalculate the new centroid
    public void calculateCentroid(){
    	
    	DNAStrand dna = new DNAStrand("");
    	
    	ArrayList<String> s = new ArrayList<String>();
   		s.add("A");
		s.add("G");
		s.add("C");
		s.add("T");
    	for(int i =0;i< 20;i++){
    		int a = 0;
        	int g = 0;
        	int c = 0;
        	int t = 0;
    		
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

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
