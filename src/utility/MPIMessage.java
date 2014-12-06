package utility;

import java.io.Serializable;
import java.util.ArrayList;

public class MPIMessage implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = -1333878324692916714L;
    public enum MessageId{
        COMMAND,
        RESPOSE,
    }
    public enum CommandId{
        CLUSTER
    }
    public enum ResponseId{
        CLUSTERRSP
    }
    private String fileName;
    private int startIndex;
    private int length;
    private MessageId msgId;
    private CommandId cmdId;
    private ResponseId rspId;
    private long runningTime;
    private int lastRun;
    private ArrayList<TwoDCluster> clusters;
    private ArrayList<TwoDPoint> rawData;
    private ArrayList<TwoDPoint> centroid;
    private ArrayList<Integer> pointNumber;
    private ArrayList<Integer> DNANumber;
    private ArrayList<DNAStrand> DNACentroid;
    private ArrayList<DNAStrandCluster> DNAClusters;
    private ArrayList<DNAStrand> DNARawData;
    public MPIMessage(){
        centroid = new ArrayList<TwoDPoint>();
        DNACentroid = new ArrayList<DNAStrand>();
        pointNumber = new ArrayList<Integer>();
        DNANumber = new ArrayList<Integer>();
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public int getLength() {
        return length;
    }
    public void setLength(int length) {
        this.length = length;
    }
    public int getStartIndex() {
        return startIndex;
    }
    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }
    public ArrayList<TwoDCluster> getClusters() {
        return clusters;
    }
    public void setClusters(ArrayList<TwoDCluster> clusters) {
        this.clusters = clusters;
    }
    public CommandId getCmdId() {
        return cmdId;
    }
    public void setCmdId(CommandId cmdId) {
        this.cmdId = cmdId;
    }
    public MessageId getMsgId() {
        return msgId;
    }
    public void setMsgId(MessageId msgId) {
        this.msgId = msgId;
    }
    public ResponseId getRspId() {
        return rspId;
    }
    public void setRspId(ResponseId rspId) {
        this.rspId = rspId;
    }
    public void addCluster(TwoDCluster c){
        clusters.add(c);
    }
    public ArrayList<TwoDPoint> getRawData() {
        return rawData;
    }
    public void setRawData(ArrayList<TwoDPoint> rawData) {
        this.rawData = rawData;
    }
    public ArrayList<TwoDPoint> getCentroid() {
        return centroid;
    }
    public void addCentroid(TwoDPoint centroid) {
        this.centroid.add(centroid);
    }
    public ArrayList<Integer> getPointNumber() {
        return pointNumber;
    }
    public void addDNANumber(int n) {
        this.DNANumber.add(n);
    }
    public ArrayList<Integer> getDNANumber() {
        return DNANumber;
    }
    public void addPointNumber(int n) {
        this.pointNumber.add(n);
    }
    public ArrayList<DNAStrand> getDNACentroid() {
        return DNACentroid;
    }
    public void addDNACentroid(DNAStrand dNACentroid) {
        DNACentroid.add(dNACentroid);
    }
    public ArrayList<DNAStrandCluster> getDNAClusters() {
        return DNAClusters;
    }
    public void setDNAClusters(ArrayList<DNAStrandCluster> dNAClusters) {
        DNAClusters = dNAClusters;
    }
    public void addDNAClusters(DNAStrandCluster dNACluster) {
        DNAClusters.add(dNACluster);
    }
    public ArrayList<DNAStrand> getDNARawData() {
        return DNARawData;
    }
    public void setDNARawData(ArrayList<DNAStrand> dNARawData) {
        DNARawData = dNARawData;
    }
    public long getRunningTime() {
        return runningTime;
    }
    public void setRunningTime(long runningTime) {
        this.runningTime = runningTime;
    }
    public int getLastRun() {
        return lastRun;
    }
    public void setLastRun(int lastRun) {
        this.lastRun = lastRun;
    }
    
}
