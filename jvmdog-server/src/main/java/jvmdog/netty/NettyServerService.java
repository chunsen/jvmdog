package jvmdog.netty;

import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jvmdog.core.protocol.ProtocolManager;
import jvmdog.protocol.api.DogServer;
import jvmdog.protocol.api.MessageHandler;
import jvmdog.service.agent.AgentService;
import jvmdog.service.client.ClientService;

@Service
public class NettyServerService {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerService.class);
    
    @Value("${jvmdog.server.port:8100}")
    private int port;
    
    @Autowired
    private List<MessageHandler> messageHandlers;
    
    @Autowired
    private ClientService clientService;
    
    @Autowired
    private AgentService agentService;
    
    private DogServer dogServer;
    
    @PostConstruct
    private void init(){
        dogServer = ProtocolManager.get().server(port);
        dogServer.onDisconnect(connection ->{
            if("client".equals(connection.getType())) {
                clientService.disconnect(connection);
            } else if("agent".equals(connection.getType())) {
                agentService.disconnect(connection);
            }
        });
        try{
            dogServer.start(messageHandlers);
        }catch(Exception e){
            logger.error("DogServer start error", e);
        }
    }
}
