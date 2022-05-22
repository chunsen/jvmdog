package jvmdog.service.client.model;

public class ClientProcess {
    private String pid;
    /**
     * 0: detached, 1: attached
     */
    private int status =0; 
    /**
     * agentName is not null only when status =1
     */
    private String agentName;
    private String mainClass;

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public int getStatus() {
        return status;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMainClass() {
        return mainClass;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }
    
}
