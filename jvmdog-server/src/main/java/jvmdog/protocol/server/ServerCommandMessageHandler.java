package jvmdog.protocol.server;

import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jvmdog.protocol.api.DogConnection;
import jvmdog.protocol.api.model.DogMessage;
import jvmdog.protocol.api.model.DogMessageType;
import jvmdog.service.agent.AgentService;
import jvmdog.service.client.ClientService;

@Service
public class ServerCommandMessageHandler implements ServerMessgeHandler{
    private static final Logger logger = LoggerFactory.getLogger(ServerCommandMessageHandler.class);

    @Autowired
    private ClientService clientService;
    
    @Autowired
    private AgentService agentService;
    
    @Override
    public void handle(DogMessage inputMessage, DogConnection connection) {
        try {
            String cmd = new String(inputMessage.getHeader(), Charset.forName("utf-8"));
            logger.info("ServerCommand: {} from {}", cmd, connection.id());
            if("client".equals(connection.getType())) {
                clientService.commandResponse(cmd, inputMessage.getData());
            } else if("agent".equals(connection.getType()))  {
                agentService.commandResponse(cmd, inputMessage.getData());
            } else {
                logger.error("unknown connection: {}, command: {}", connection.getType(), cmd);
            }
        }catch(Exception e) {
            logger.error("handle command error", e);
        }
    }

    @Override
    public int type() {
        return DogMessageType.CLIENT_COMMAND_RESPONSE.getValue();
    }

}
