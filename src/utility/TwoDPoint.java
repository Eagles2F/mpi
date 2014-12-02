package utility;
import java.io.Serializable;

public class TwoDPoint implements Serializable, DataTypeBase{
    private double x;
    private double y;
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
    @Override
    public DataTypeBase add(DataTypeBase pt) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public DataTypeBase distance(DataTypeBase pt) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public DataTypeBase div(DataTypeBase pt, double divisor) {
        // TODO Auto-generated method stub
        return null;
    }
    
}