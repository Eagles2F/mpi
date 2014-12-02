package utility;

import java.io.Serializable;
import java.util.ArrayList;

public class TowDCluster implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private double xMean;
    private double yMean;
    private ArrayList<TwoDPoint> cluster;
    public TowDCluster(){
        cluster = new ArrayList<TwoDPoint>();
        xMean = 0;
        yMean = 0;
    }
    public double getxMean() {
        return xMean;
    }
    public void setxMean(double xMean) {
        this.xMean = xMean;
    }
    public double getyMean() {
        return yMean;
    }
    public void setyMean(double yMean) {
        this.yMean = yMean;
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
    public void addCluster(TowDCluster newCluster){
        cluster.addAll(newCluster.getCluster());
    }
    public void calculateMean(){
        int count = cluster.size();
        if(count == 0) {
            return;
        }
        xMean = 0;
        yMean = 0;
        for(TwoDPoint pt : cluster) {
            xMean += pt.getX();
            yMean += pt.getY();
        }
        xMean = xMean/count;
        yMean = yMean/count;
    }
}