package jvmdog.protocol.api.model;

public class HeartBeatData extends MessageData{
    private String type;
    private String pid;
    
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getPid() {
        return pid;
    }
    public void setPid(String pid) {
        this.pid = pid;
    }
    
}
