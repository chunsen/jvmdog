package jvmdog.service.agent;

public class Agent {
    private String ip;
    private String name;
    private String peerPid;
    private String args;
    
    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPeerPid() {
        return peerPid;
    }
    public void setPeerPid(String peerPid) {
        this.peerPid = peerPid;
    }
    public String getArgs() {
        return args;
    }
    public void setArgs(String args) {
        this.args = args;
    }
    @Override
    public String toString() {
        return "{ip:" + ip + ", name:" + name + ", peerPid:" + peerPid + ", args:" + args + "}";
    }
    
}
