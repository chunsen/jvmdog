package jvmdog.service.client.model;

public class Client {
    private String ip;
    private String name;
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
    public String getArgs() {
        return args;
    }
    public void setArgs(String args) {
        this.args = args;
    }
    @Override
    public String toString() {
        return "{ip:" + ip + ", name:" + name + ", args:" + args + "}";
    }
}
