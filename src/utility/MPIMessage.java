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
    private ArrayList<TwoDCluster> clusters;
    private ArrayList<TwoDPoint> rawData;
    private TwoDPoint centroid;
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
    public TwoDPoint getCentroid() {
        return centroid;
    }
    public void setCentroid(TwoDPoint centroid) {
        this.centroid = centroid;
    }
    
}