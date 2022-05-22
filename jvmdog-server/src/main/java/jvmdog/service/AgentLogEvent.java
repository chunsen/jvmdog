package jvmdog.service;

public class AgentLogEvent {
    private final String agentId;
    private final String log;
    
    public AgentLogEvent(String agentId, String log) {
        super();
        this.agentId = agentId;
        this.log = log;
    }
    
    public String getAgentId() {
        return agentId;
    }
    public String getLog() {
        return log;
    }

}
