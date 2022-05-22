package jvmdog.client;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jvmdog.client.core.command.ClientCommand;
import jvmdog.protocol.api.DogConnection;
import jvmdog.protocol.api.MessageHandler;
import jvmdog.protocol.api.model.DogMessage;
import jvmdog.protocol.api.model.DogMessageType;

@Service
public class ClientCommandMessageHandler implements MessageHandler{
    private static final Logger logger = LoggerFactory.getLogger(ClientCommandMessageHandler.class);
    
    private final Map<String, ClientCommand> commandMap;
    
    public ClientCommandMessageHandler(Map<String, ClientCommand> beanMap){
        this.commandMap = new HashMap<>();
        for(ClientCommand clientCommand: beanMap.values()) {
            this.commandMap.put(clientCommand.name(), clientCommand);
        }
    }
    
    @Override
    public void handle(DogMessage inputMessage, DogConnection connection) {
        String cmd = new String(inputMessage.getHeader(), Charset.forName("utf-8"));
        logger.info("ClientCommand: {}", cmd);
        if("detach".equals(cmd)){
            connection.close();
        }
        
        ClientCommand clientCommand = commandMap.get(cmd);
        if(clientCommand == null){
            logger.error("ClientCommand is null for {}", cmd);
        } else {
            DogMessage result = clientCommand.run(inputMessage.getData());
            if(result != null){
                connection.send(result);
            }
        }

    }

    @Override
    public int type() {
        return DogMessageType.CLIENT_COMMAND.getValue();
    }

}
