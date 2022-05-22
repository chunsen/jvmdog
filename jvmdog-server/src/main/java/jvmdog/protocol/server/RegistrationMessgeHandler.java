package jvmdog.protocol.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jvmdog.protocol.api.DogConnection;
import jvmdog.protocol.api.model.DogMessage;
import jvmdog.protocol.api.model.DogMessageType;
import jvmdog.protocol.api.model.RegistrationData;
import jvmdog.protocol.api.model.Version;
import jvmdog.protocol.api.utils.SerializeUtils;
import jvmdog.service.agent.AgentService;
import jvmdog.service.client.ClientService;

@Service
public class RegistrationMessgeHandler implements ServerMessgeHandler{
    private static final Logger logger = LoggerFactory.getLogger(RegistrationMessgeHandler.class);
    
    @Autowired
    private AgentService agentService;
    
    @Autowired
    private ClientService clisentService;
    

    @Override
    public void handle(DogMessage inputMessage, DogConnection connection) {
        if(!Version.suuport(inputMessage.getVersion())) {
            logger.error("Remote {} version {} not support, current version is {}", connection.id(), inputMessage.getVersion(), Version.CURRENT_VERSION);
            return;
        }
        
        RegistrationData registrationMessage = SerializeUtils.deserialize(inputMessage.getData(), RegistrationData.class);
        logger.info("Receive Registration from :{}@{}", registrationMessage.getPid(), connection.ip());
        String peerType = registrationMessage.getType();
        if("client".equals(peerType)){
            clisentService.add(connection);
        } else if("agent".equals(peerType)){
            agentService.add(connection);
        }
    }

    @Override
    public int type() {
        return DogMessageType.REGISTRATION.getValue();
    }
}
