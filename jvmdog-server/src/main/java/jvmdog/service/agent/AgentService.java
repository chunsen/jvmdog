package jvmdog.service.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import jvmdog.core.protocol.agent.AgentCommandManager;
import jvmdog.protocol.api.DogConnection;
import jvmdog.service.BaseService;
import jvmdog.service.ResponseDataHandler;

@Service
public class AgentService extends BaseService {
    
    private final Map<String, AgentResponseDataHandler> handlerMap = new ConcurrentHashMap<>();
    
    public AgentService(List<AgentResponseDataHandler> handlers) {
        super(AgentCommandManager.Instance.getCommands());
        
        for(AgentResponseDataHandler handler: handlers) {
            handlerMap.put(handler.name(), handler);
        }
    }
    
    public Agent getAgent(String name) {
        DogConnection connection = connectionMap.get(name);
        if(connection == null) {
            return null;
        }
        
        Agent agent = toAgent(connection);
        agent.setName(name);
        return agent;
    }

    public List<Agent> getAll() {
        List<Agent> result = new ArrayList<>();
        for (Entry<String, DogConnection> entry : connectionMap.entrySet()) {
            Agent agent = toAgent(entry.getValue());
            agent.setName(entry.getKey());
            result.add(agent);
        }

        return result;
    }
    
    public List<Agent> getByIp(String ip) {
        List<Agent> result = new ArrayList<>();
        for (Entry<String, DogConnection> entry : connectionMap.entrySet()) {
            if(!entry.getValue().ip().equals(ip)) {
                continue;
            }
            Agent agent = toAgent(entry.getValue());
            agent.setName(entry.getKey());
            result.add(agent);
        }

        return result;
    }
    
    private Agent toAgent(DogConnection connection) {
        Agent agent = new Agent();
        agent.setPeerPid(connection.peerPid());
        agent.setIp(connection.ip());
        return agent;
    }
    
    protected ResponseDataHandler getResponseDataHandler(String command) {
        return handlerMap.get(command);
    }
}
