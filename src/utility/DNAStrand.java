package utility;
import java.io.Serializable;

public class DNAStrand implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String strand;
    
    public DNAStrand(String s){
    	this.strand = s;
    }
    
    public String getStrand() {
		return strand;
	}

	public void setStrand(String strand) {
		this.strand = strand;
	}

	public int distance(DNAStrand dna){//the number of different characters in two strands is the distance
       int dis = 0; 
       for(int i = 0;i<strand.length();i++){
    	   if(dna.getStrand().charAt(i) != this.strand.charAt(i)){
    		   dis++;
    	   }
       }
       return dis;
    }
 
}