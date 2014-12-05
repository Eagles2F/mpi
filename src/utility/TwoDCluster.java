package utility;

import java.io.Serializable;
import java.util.ArrayList;

public class TwoDCluster implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private TwoDPoint centroid;
    
    private ArrayList<TwoDPoint> cluster;
    public TwoDCluster(){
        cluster = new ArrayList<TwoDPoint>();
       setCentroid(null);
    }
 
    public ArrayList<TwoDPoint> getCluster() {
        return cluster;
    }
    public void setCluster(ArrayList<TwoDPoint> cluster) {
        this.cluster = cluster;
    }
    public void add(TwoDPoint pt){
        cluster.add(pt);
    }
    public void addCluster(TwoDCluster newCluster){
        cluster.addAll(newCluster.getCluster());
    }
    
    //recaculate the new centroid
    public void calculateCentroid(){
        int count = cluster.size();
        if(count == 0) {
            return;
        }
        double xMean = 0;
        double yMean = 0;
        for(TwoDPoint pt : cluster) {
            xMean += pt.getX();
            yMean += pt.getY();
        }
        xMean = xMean/count;
        yMean = yMean/count;
        if(centroid == null){
            centroid = new TwoDPoint();
        }
        centroid.setX(xMean);
        centroid.setY(yMean);
    }
    
	public TwoDPoint getCentroid() {
		return centroid;
	}
	public void setCentroid(TwoDPoint centroid) {
		this.centroid = centroid;
	}
	
	public void clearCluster(){
		this.cluster.clear();
	}
}
