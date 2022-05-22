package jvmdog.core.protocol.agent;

import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jvmdog.protocol.api.DogConnection;
import jvmdog.protocol.api.MessageHandler;
import jvmdog.protocol.api.model.DogMessage;
import jvmdog.protocol.api.model.DogMessageType;
import jvmdog.protocol.api.model.MessageData;
import jvmdog.protocol.api.model.ResponseMessageData;
import jvmdog.protocol.api.utils.SerializeUtils;

public class AgentStopCommandMessageHandler implements MessageHandler{
    private static final Logger logger = LoggerFactory.getLogger(AgentStopCommandMessageHandler.class);
    
    private final AgentContext agentContext;
    
    public AgentStopCommandMessageHandler(AgentContext agentContext){
        this.agentContext = agentContext;
    }
    
    @Override
    public void handle(DogMessage inputMessage, DogConnection connection) {
        String cmd = new String(inputMessage.getHeader(), Charset.forName("utf-8"));
        logger.info("Stop AgentCommand: {}", cmd);

        MessageData requestData = SerializeUtils.deserialize(inputMessage.getData(), MessageData.class);
        ResponseMessageData responseData = new ResponseMessageData();
        responseData.setId(requestData.getId());
        
        agentContext.stopSession(requestData.getId());
        
        byte[] messageData = SerializeUtils.serialize(responseData);
        DogMessage message = DogMessage.clientCommandStopResponse(cmd);
        message.setData(messageData);
        
        connection.send(message);
    }
    
    @Override
    public int type() {
        return DogMessageType.CLIENT_COMMAND_STOP.getValue();
    }


}
