package utility;
import java.io.Serializable;

public class TwoDPoint implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private double x;
    private double y;
    
    public TwoDPoint(){
    	
    }
    
    public TwoDPoint(double x , double y){
    	this.x = x;
    	this.y = y;
    }
    
    public double getX() {
        return x;
    }
    public void setX(double x) {
        this.x = x;
    }
    public double getY() {
        return y;
    }
    public void setY(double y) {
        this.y = y;
    }
    
    public double distance(TwoDPoint pt){
        double dis;
        dis = Math.sqrt(this.x - pt.getX()) + Math.sqrt(this.y - pt.getY());
        return dis;
    }
    
    public TwoDPoint pointAdd(TwoDPoint pt){
        TwoDPoint newP = new TwoDPoint();
        newP.setX(this.x + pt.getX());
        newP.setY(this.y + pt.getY());
        return newP;
    }

    
}