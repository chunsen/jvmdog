package jvmdog.core.protocol.agent;

import jvmdog.protocol.api.RemoteCommand;
import jvmdog.protocol.api.model.DogMessage;

public interface AgentCommand extends RemoteCommand{
    DogMessage run(AgentContext agentContext, byte[] data);
}
