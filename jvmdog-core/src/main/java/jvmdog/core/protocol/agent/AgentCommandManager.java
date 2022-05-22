package jvmdog.core.protocol.agent;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class AgentCommandManager {
    public static final AgentCommandManager Instance = new AgentCommandManager();
    
    private final Map<String, AgentCommand> commandMap = new HashMap<>();
    
    private AgentCommandManager() {
        ServiceLoader<AgentCommand> services =  ServiceLoader.load(AgentCommand.class);
        for(AgentCommand command: services) {
            commandMap.put(command.name(), command);
        }
    }
    
    public AgentCommand get(String name) {
        return commandMap.get(name);
    }
    
    public AgentCommand[] getCommands() {
        return commandMap.values().toArray(new AgentCommand[commandMap.size()]);
    }
}
