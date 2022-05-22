package jvmdog.protocol.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import jvmdog.protocol.api.DogConnection;
import jvmdog.protocol.api.model.DogMessage;
import jvmdog.protocol.api.model.DogMessageType;
import jvmdog.protocol.api.utils.SerializeUtils;
import jvmdog.service.AgentLogEvent;

@Service
public class LogMessgeHandler implements ServerMessgeHandler{
    private static final Logger logger = LoggerFactory.getLogger("jvmdog.clientLogger");
    public static final String REMOTE_ID="remoteId";
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    @Override
    public void handle(DogMessage inputMessage, DogConnection connection) {
        String log = SerializeUtils.fromBytes(inputMessage.getData());
        try {
            MDC.put(REMOTE_ID, connection.getType()+ "_"+connection.id());
            logger.info(log);
        }finally {
            MDC.remove(REMOTE_ID);
        }
        
        eventPublisher.publishEvent(new AgentLogEvent(connection.id(), log));
    }

    @Override
    public int type() {
        return DogMessageType.LOG.getValue();
    }
}
