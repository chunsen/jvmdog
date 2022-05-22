package jvmdog.core.protocol.agent;

import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jvmdog.protocol.api.DogConnection;
import jvmdog.protocol.api.MessageHandler;
import jvmdog.protocol.api.model.DogMessage;
import jvmdog.protocol.api.model.DogMessageType;

public class AgentCommandMessageHandler implements MessageHandler{
    private static final Logger logger = LoggerFactory.getLogger(AgentCommandMessageHandler.class);
    
    private final AgentContext agentContext;
    
    public AgentCommandMessageHandler(AgentContext agentContext){
        this.agentContext = agentContext;
    }
    
    @Override
    public void handle(DogMessage inputMessage, DogConnection connection) {
        String cmd = new String(inputMessage.getHeader(), Charset.forName("utf-8"));
        logger.info("AgentCommand: {}", cmd);

        AgentCommand clientCommand = AgentCommandManager.Instance.get(cmd);
        if(clientCommand == null){
            logger.warn("invalid agentCommand: {}", cmd);
            return;
        }
        
        DogMessage result = clientCommand.run(agentContext, inputMessage.getData());
        if(result != null){
            connection.send(result);
        }
    }
    
    @Override
    public int type() {
        return DogMessageType.CLIENT_COMMAND.getValue();
    }


}
