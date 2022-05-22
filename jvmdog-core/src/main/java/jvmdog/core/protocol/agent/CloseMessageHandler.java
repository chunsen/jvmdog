package jvmdog.core.protocol.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jvmdog.protocol.api.DogConnection;
import jvmdog.protocol.api.MessageHandler;
import jvmdog.protocol.api.model.DogMessage;
import jvmdog.protocol.api.model.DogMessageType;

public class CloseMessageHandler implements MessageHandler{
    private static final Logger logger = LoggerFactory.getLogger(CloseMessageHandler.class);
    
    @Override
    public void handle(DogMessage inputMessage, DogConnection connection) {
        connection.close();
        logger.info("connection closed.");
    }

    @Override
    public int type() {
        return DogMessageType.CLOSE_BY_SERVER.getValue();
    }


}
