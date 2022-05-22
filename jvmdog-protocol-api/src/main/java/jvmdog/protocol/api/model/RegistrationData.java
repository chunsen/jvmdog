package jvmdog.protocol.api.model;

public class RegistrationData extends MessageData{
    private String type;
    private String pid;
    private OSInfo osInfo;
    
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
    public OSInfo getOsInfo() {
        return osInfo;
    }
    public void setOsInfo(OSInfo osInfo) {
        this.osInfo = osInfo;
    }
    
}
